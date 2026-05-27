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
import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.repository.PatientRepository;
import com.diploma.aerodent.data.repository.ProcedureLogRepository;
import com.diploma.aerodent.data.repository.ToothStatusRepository;
import com.diploma.aerodent.util.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DentalChartViewModel extends AndroidViewModel {

    private final ToothStatusRepository repository;
    private final PatientRepository patientRepository;
    private final ProcedureLogRepository procedureLogRepository;
    private final MutableLiveData<Integer> patientId = new MutableLiveData<>();
    private final MutableLiveData<Integer> appointmentId = new MutableLiveData<>();
    private final LiveData<List<ToothStatus>> toothStatuses;
    private final LiveData<String> toothColorsJson;
    private final LiveData<Patient> patient;
    private final LiveData<String> patientName;
    private final LiveData<List<ProcedureLog>> procedureLogs;
    private final SessionManager sessionManager;

    public static final List<DentalCondition> IMPLANT_CONFLICTS = java.util.Collections.unmodifiableList(
        java.util.Arrays.asList(
            DentalCondition.CARIES,
            DentalCondition.PULPITIS,
            DentalCondition.ROOT_CANAL,
            DentalCondition.RADICULAR_POST,
            DentalCondition.OBTURATION,
            DentalCondition.CALCULUS,
            DentalCondition.PERIODONTITIS,
            DentalCondition.PERIODONTITIS_PA
        )
    );

    public static final List<DentalCondition> MISSING_CONFLICTS;
    static {
        List<DentalCondition> list = new ArrayList<>();
        for (DentalCondition c : DentalCondition.values()) {
            if (c != DentalCondition.MISSING &&
                c != DentalCondition.IMPLANT &&
                c != DentalCondition.PONTIC_FIXED &&
                c != DentalCondition.PONTIC_REMOVABLE &&
                c != DentalCondition.SUPERNUMERARY) {
                list.add(c);
            }
        }
        MISSING_CONFLICTS = java.util.Collections.unmodifiableList(list);
    }

    public DentalChartViewModel(
            @NonNull Application application,
            ToothStatusRepository repository,
            PatientRepository patientRepository,
            ProcedureLogRepository procedureLogRepository) {
        super(application);
        this.repository = repository;
        this.patientRepository = patientRepository;
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
            return com.diploma.aerodent.util.NameUtils.formatFirstLastName(p);
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

    public void updateToothStatus(int toothNumber, DentalCondition condition, String surfaces, boolean isCurrentAppointment) {
        Integer pid = patientId.getValue();
        if (pid == null)
            return;

        String currentUserId = sessionManager.getLoggedInUserId();
        String currentUserName = sessionManager.getLoggedInUserName();
        String currentUserRole = sessionManager.getLoggedInUserRole();

        String formattedCreatorName = currentUserName;
        if (currentUserName != null && currentUserRole != null) {
            String lowerName = currentUserName.toLowerCase();
            if (currentUserRole.equals("DENTIST") || currentUserRole.equals("ADMIN")) {
                if (!lowerName.startsWith("д-р")) {
                    formattedCreatorName = "д-р " + currentUserName;
                }
            } else if (currentUserRole.equals("ASSISTANT")) {
                if (!lowerName.startsWith("ас.")) {
                    formattedCreatorName = "ас. " + currentUserName;
                }
            }
        }

        Integer apptId = isCurrentAppointment ? appointmentId.getValue() : null;
        ToothStatus newStatus = new ToothStatus(pid, toothNumber, condition, surfaces, new Date(), apptId);
        newStatus.setUserId(currentUserId);
        newStatus.setCreatorName(formattedCreatorName);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getDatabase(getApplication()).runInTransaction(() -> {
                List<ToothStatus> currentStatuses = repository.getToothStatusesForToothSync(pid, toothNumber);

                // Clear conflicting conditions based on new condition
                if (condition == DentalCondition.HEALTHY) {
                    repository.deleteAllStatusesForTooth(pid, toothNumber);
                } else if (condition == DentalCondition.MISSING) {
                    repository.deleteSpecificStatusesForTooth(pid, toothNumber, MISSING_CONFLICTS);
                } else if (condition == DentalCondition.IMPLANT ||
                           condition == DentalCondition.PONTIC_FIXED ||
                           condition == DentalCondition.PONTIC_REMOVABLE) {
                    repository.deleteSpecificStatusesForTooth(pid, toothNumber, IMPLANT_CONFLICTS);
                } else if (condition == DentalCondition.ROOT_CANAL) {
                    repository.deleteStatus(pid, toothNumber, DentalCondition.PULPITIS);
                } else if (condition == DentalCondition.CROWN) {
                    repository.deleteStatus(pid, toothNumber, DentalCondition.CARIES);
                    repository.deleteStatus(pid, toothNumber, DentalCondition.FRACTURE);
                    repository.deleteStatus(pid, toothNumber, DentalCondition.OBTURATION);
                } else if (condition == DentalCondition.OBTURATION) {
                    if (surfaces != null && !surfaces.isEmpty()) {
                        String[] fillSurfaces = surfaces.split(",");
                        Set<String> filledSet = new HashSet<>();
                        for (String fs : fillSurfaces) {
                            filledSet.add(fs.trim());
                        }

                        for (ToothStatus status : currentStatuses) {
                            DentalCondition currentCond = status.getCondition();
                            if (currentCond == DentalCondition.CARIES || currentCond == DentalCondition.FRACTURE) {
                                String currentSurfacesStr = status.getSurfaces();
                                if (currentSurfacesStr != null && !currentSurfacesStr.isEmpty()) {
                                    String[] currentSurfaces = currentSurfacesStr.split(",");
                                    List<String> remaining = new ArrayList<>();
                                    for (String s : currentSurfaces) {
                                        if (!filledSet.contains(s.trim())) {
                                            remaining.add(s.trim());
                                        }
                                    }

                                    if (remaining.isEmpty()) {
                                        repository.deleteStatus(pid, toothNumber, currentCond);
                                    } else {
                                        String remainingStr = android.text.TextUtils.join(",", remaining);
                                        status.setSurfaces(remainingStr);
                                        repository.update(status);
                                    }
                                }
                            }
                        }
                    }
                }

                repository.insert(newStatus);
            });
        });

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

    public void updateToothStatus(int toothNumber, DentalCondition condition, List<String> selectedSurfaces, boolean isCurrentAppointment) {
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

    public String checkConditionConflict(List<ToothStatus> currentStatuses, DentalCondition newCondition, android.content.Context context) {
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
            } else if (c == DentalCondition.IMPLANT || c == DentalCondition.PONTIC_FIXED || c == DentalCondition.PONTIC_REMOVABLE) {
                hasImplantOrPontic = true;
            } else if (c == DentalCondition.ROOT_CANAL) {
                hasRootCanal = true;
            }
        }

        if (hasMissing) {
            if (newCondition != DentalCondition.HEALTHY && MISSING_CONFLICTS.contains(newCondition)) {
                return context.getString(com.diploma.aerodent.R.string.conflict_missing_tooth);
            }
        }

        if (hasImplantOrPontic) {
            if (IMPLANT_CONFLICTS.contains(newCondition)) {
                return context.getString(com.diploma.aerodent.R.string.conflict_implant_tooth);
            }
        }

        if (newCondition == DentalCondition.PULPITIS && hasRootCanal) {
            return context.getString(com.diploma.aerodent.R.string.conflict_root_canal_pulpitis);
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
