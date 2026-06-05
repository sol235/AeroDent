package com.diploma.aerodent.ui.dentalchart;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.entity.ProcedureLog;
import com.diploma.aerodent.data.local.model.DentalCondition;
import com.diploma.aerodent.data.repository.PatientRepository;
import com.diploma.aerodent.data.repository.ProcedureLogRepository;
import com.diploma.aerodent.data.repository.ToothStatusRepository;
import com.diploma.aerodent.util.NameUtils;
import com.diploma.aerodent.util.SessionManager;
import com.diploma.aerodent.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DentalChartViewModel extends AndroidViewModel {

    private final ToothStatusRepository repository;
    private final ProcedureLogRepository procedureLogRepository;
    private final MutableLiveData<Integer> patientId = new MutableLiveData<>();
    private final MutableLiveData<Integer> appointmentId = new MutableLiveData<>();
    private final LiveData<List<ToothStatus>> toothStatuses;
    private final LiveData<String> toothColorsJson;
    private final LiveData<Patient> patient;
    private final LiveData<String> patientName;
    private final LiveData<List<ProcedureLog>> procedureLogs;
    private final SessionManager sessionManager;

    public DentalChartViewModel(@NonNull Application application, ToothStatusRepository repository,
            PatientRepository patientRepository, ProcedureLogRepository procedureLogRepository) {
        super(application);
        this.repository = repository;
        this.procedureLogRepository = procedureLogRepository;
        sessionManager = new SessionManager(application);

        toothStatuses = Transformations.switchMap(patientId, repository::getToothStatusesForPatient);
        patient = Transformations.switchMap(patientId, patientRepository::getPatientById);
        procedureLogs = Transformations.switchMap(patientId, procedureLogRepository::getProcedureLogsForPatient);

        toothColorsJson = Transformations.map(toothStatuses, statuses -> {
            JSONObject jsonObject = new JSONObject();
            if (statuses == null)
                return jsonObject.toString();

            Map<Integer, String> colors = new HashMap<>();
            Map<Integer, Integer> priorities = new HashMap<>();

            for (ToothStatus status : statuses) {
                int toothNum = status.getToothNumber();
                DentalCondition condition = status.getCondition();
                String color = condition.getColorHex(getApplication());

                int priority = condition.getPriority();
                if (!priorities.containsKey(toothNum) || priority > priorities.get(toothNum)) {
                    priorities.put(toothNum, priority);
                    colors.put(toothNum, color);
                }
            }

            for (Map.Entry<Integer, String> entry : colors.entrySet()) {
                if (entry.getValue() != null) {
                    try {
                        jsonObject.put(String.valueOf(entry.getKey()), entry.getValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return jsonObject.toString();
        });

        patientName = Transformations.map(patient, p -> {
            if (p == null)
                return "";
            return NameUtils.formatFirstLastName(p);
        });
    }

    public void setPatientId(int id) {
        patientId.setValue(id);
    }

    public void setAppointmentId(Integer id) {
        appointmentId.setValue(id);
    }

    public LiveData<Integer> getAppointmentId() {
        return appointmentId;
    }

    public LiveData<List<ToothStatus>> getToothStatuses() {
        return toothStatuses;
    }

    public LiveData<Patient> getPatient() {
        return patient;
    }

    public LiveData<String> getPatientName() {
        return patientName;
    }

    public LiveData<String> getToothColorsJson() {
        return toothColorsJson;
    }

    public LiveData<List<ProcedureLog>> getProcedureLogs() {
        return procedureLogs;
    }

    public void updateToothStatus(int toothNumber, DentalCondition condition, String surfaces,
            boolean isCurrentAppointment) {
        Integer pid = patientId.getValue();
        if (pid == null)
            return;

        String currentUserId = sessionManager.getLoggedInUserId();
        String formattedCreatorName = sessionManager.getFormattedLoggedInUserName();

        Integer apptId = isCurrentAppointment ? appointmentId.getValue() : null;
        ToothStatus newStatus = new ToothStatus(pid, toothNumber, condition, surfaces, new Date(), apptId);
        newStatus.setUserId(currentUserId);
        newStatus.setCreatorName(formattedCreatorName);

        repository.updateToothStatusWithConflicts(pid, toothNumber, condition, surfaces, newStatus);

        // Save history in ProcedureLog
        if (condition != DentalCondition.HEALTHY) {
            ProcedureLog log = new ProcedureLog();
            log.setPatientId(pid);
            log.setToothNumber(toothNumber);
            log.setUserId(currentUserId);
            log.setCreatorName(formattedCreatorName);

            Integer activeApptId = appointmentId.getValue();
            if (isCurrentAppointment && activeApptId != null) {
                log.setAppointmentId(activeApptId);
                log.setEntryType(ProcedureLog.TYPE_PROCEDURE);
            } else {
                log.setAppointmentId(null);
                log.setEntryType(ProcedureLog.TYPE_STATUS);
            }
            log.setDateLogged(new Date());

            log.setDiagnosis(condition.getDisplayName(getApplication()));
            log.setActionTaken(condition.name());
            log.setSurfaces(surfaces);
            procedureLogRepository.insert(log);
        }
    }


    public void updateProcedureLog(ProcedureLog log) {
        procedureLogRepository.update(log);
    }

    public void updateToothStatus(int toothNumber, DentalCondition condition, List<String> selectedSurfaces,
            boolean isCurrentAppointment) {
        String surfaces = "";
        if (selectedSurfaces != null && !selectedSurfaces.isEmpty()) {
            surfaces = android.text.TextUtils.join(",", selectedSurfaces);
        }
        updateToothStatus(toothNumber, condition, surfaces, isCurrentAppointment);
    }

    public String[] getSurfaceCodes() {
        return ToothStatus.ALL_SURFACES;
    }

    public void deleteToothStatus(int toothNumber, DentalCondition condition) {
        Integer pid = patientId.getValue();
        if (pid == null)
            return;
        repository.deleteStatus(pid, toothNumber, condition);
    }

    public LiveData<List<ToothStatus>> getToothStatusesForTooth(int toothNumber) {
        Integer pid = patientId.getValue();
        if (pid == null)
            return new MutableLiveData<>();
        return repository.getToothStatusesForTooth(pid, toothNumber);
    }

    public Map<DentalCondition.Category, List<DentalCondition>> getGroupedConditions() {
        Map<DentalCondition.Category, List<DentalCondition>> grouped = new HashMap<>();
        for (DentalCondition condition : DentalCondition.values()) {
            if (condition == DentalCondition.HEALTHY)
                continue;
            List<DentalCondition> list = grouped.computeIfAbsent(condition.getCategory(), k -> new ArrayList<>());
            list.add(condition);
        }
        return grouped;
    }

    public List<DentalCondition.Category> getVisibleCategories() {
        List<DentalCondition.Category> visible = new ArrayList<>();
        for (DentalCondition.Category category : DentalCondition.Category.values()) {
            if (category != DentalCondition.Category.GENERAL) {
                visible.add(category);
            }
        }
        return visible;
    }

    public String checkConditionConflict(List<ToothStatus> currentStatuses, DentalCondition newCondition,
            android.content.Context context) {
        if (currentStatuses == null || currentStatuses.isEmpty()) {
            return null;
        }

        boolean hasMissing = false;
        boolean hasImplantOrPontic = false;
        boolean hasRootCanal = false;

        for (ToothStatus status : currentStatuses) {
            DentalCondition c = status.getCondition();
            if (c == DentalCondition.MISSING) {
                hasMissing = true;
            } else if (c == DentalCondition.IMPLANT || c == DentalCondition.PONTIC_FIXED
                    || c == DentalCondition.PONTIC_REMOVABLE) {
                hasImplantOrPontic = true;
            } else if (c == DentalCondition.ROOT_CANAL) {
                hasRootCanal = true;
            }
        }

        if (hasMissing) {
            if (newCondition != DentalCondition.HEALTHY
                    && ToothStatusRepository.MISSING_CONFLICTS.contains(newCondition)) {
                return context.getString(R.string.conflict_missing_tooth);
            }
        }

        if (hasImplantOrPontic) {
            if (ToothStatusRepository.IMPLANT_CONFLICTS.contains(newCondition)) {
                return context.getString(R.string.conflict_implant_tooth);
            }
        }

        if (newCondition == DentalCondition.PULPITIS && hasRootCanal) {
            return context.getString(R.string.conflict_root_canal_pulpitis);
        }

        return null;
    }

    public List<String> getExistingSurfaces(List<ToothStatus> currentStatuses, DentalCondition condition) {
        List<String> existingSurfaces = new ArrayList<>();
        if (currentStatuses != null) {
            for (ToothStatus status : currentStatuses) {
                if (status.getCondition() == condition) {
                    String surfacesStr = status.getSurfaces();
                    if (surfacesStr != null && !surfacesStr.isEmpty()) {
                        java.util.Collections.addAll(existingSurfaces, surfacesStr.split(","));
                    }
                    break;
                }
            }
        }
        return existingSurfaces;
    }
}
