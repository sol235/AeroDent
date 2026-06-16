package com.diploma.aerodent.data.repository;

import androidx.lifecycle.LiveData;
import com.diploma.aerodent.data.local.AppDatabase;
import com.diploma.aerodent.data.local.dao.ToothStatusDao;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.model.DentalCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ToothStatusRepository {

    private final ToothStatusDao toothStatusDao;
    private final AppDatabase database;

    public static final List<DentalCondition> IMPLANT_CONFLICTS = List.of(
        DentalCondition.CARIES,
        DentalCondition.PULPITIS,
        DentalCondition.ROOT_CANAL,
        DentalCondition.RADICULAR_POST,
        DentalCondition.OBTURATION,
        DentalCondition.CALCULUS,
        DentalCondition.PERIODONTITIS,
        DentalCondition.PERIODONTITIS_PA
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
        MISSING_CONFLICTS = Collections.unmodifiableList(list);
    }

    public ToothStatusRepository(ToothStatusDao toothStatusDao, AppDatabase database) {
        this.toothStatusDao = toothStatusDao;
        this.database = database;
    }

    public LiveData<List<ToothStatus>> getToothStatusesForPatient(int patientId) {
        return toothStatusDao.getToothStatusesForPatient(patientId);
    }

    public LiveData<List<ToothStatus>> getToothStatusesForTooth(int patientId, int toothNumber) {
        return toothStatusDao.getToothStatusesForTooth(patientId, toothNumber);
    }

    public List<ToothStatus> getToothStatusesForToothSync(int patientId, int toothNumber) {
        return toothStatusDao.getToothStatusesForToothSync(patientId, toothNumber);
    }

    public List<ToothStatus> getAllToothStatusesSync() {
        return toothStatusDao.getAllToothStatusesSync();
    }

    public void insert(ToothStatus toothStatus) {
        toothStatusDao.insert(toothStatus);
    }

    public void update(ToothStatus toothStatus) {
        toothStatusDao.update(toothStatus);
    }

    public void deleteStatus(int patientId, int toothNumber, DentalCondition condition) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            deleteStatusSync(patientId, toothNumber, condition);
        });
    }

    private void deleteStatusSync(int patientId, int toothNumber, DentalCondition condition) {
        toothStatusDao.deleteStatus(patientId, toothNumber, condition);
    }

    public void deleteAllStatusesForTooth(int patientId, int toothNumber) {
        toothStatusDao.deleteAllStatusesForTooth(patientId, toothNumber);
    }

    public void deleteSpecificStatusesForTooth(int patientId, int toothNumber, List<DentalCondition> conditions) {
        toothStatusDao.deleteSpecificStatusesForTooth(patientId, toothNumber, conditions);
    }

    public void updateToothStatusWithConflicts(int patientId, int toothNumber, DentalCondition condition, String surfaces, ToothStatus newStatus) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.runInTransaction(() -> {
                List<ToothStatus> currentStatuses = getToothStatusesForToothSync(patientId, toothNumber);
                clearConflicts(patientId, toothNumber, condition, surfaces, currentStatuses);
                insert(newStatus);
            });
        });
    }

    private void clearConflicts(int patientId, int toothNumber, DentalCondition condition, String surfaces, List<ToothStatus> currentStatuses) {
        switch (condition) {
        case HEALTHY:
            deleteAllStatusesForTooth(patientId, toothNumber);
            break;
        case MISSING:
            deleteSpecificStatusesForTooth(patientId, toothNumber, MISSING_CONFLICTS);
            break;
        case IMPLANT:
        case PONTIC_FIXED:
        case PONTIC_REMOVABLE:
            deleteSpecificStatusesForTooth(patientId, toothNumber, IMPLANT_CONFLICTS);
            break;
        case ROOT_CANAL:
            deleteStatusSync(patientId, toothNumber, DentalCondition.PULPITIS);
            break;
        case CROWN:
            deleteStatusSync(patientId, toothNumber, DentalCondition.CARIES);
            deleteStatusSync(patientId, toothNumber, DentalCondition.FRACTURE);
            deleteStatusSync(patientId, toothNumber, DentalCondition.OBTURATION);
            break;
        case OBTURATION:
            clearObturationConflicts(patientId, toothNumber, surfaces, currentStatuses);
            break;
        default:
            break;
        }
    }

    private void clearObturationConflicts(int patientId, int toothNumber, String surfaces, List<ToothStatus> currentStatuses) {
        if (surfaces == null || surfaces.isEmpty()) {
            return;
        }

        String[] fillSurfaces = surfaces.split(",");
        Set<String> filledSet = new HashSet<>();
        for (String fs : fillSurfaces) {
            filledSet.add(fs.trim());
        }

        for (ToothStatus status : currentStatuses) {
            DentalCondition currentCond = status.getCondition();
            if (currentCond == DentalCondition.CARIES || currentCond == DentalCondition.FRACTURE) {
                clearSurfacesFromCondition(patientId, toothNumber, status, currentCond, filledSet);
            }
        }
    }

    private void clearSurfacesFromCondition(int patientId, int toothNumber, ToothStatus status, DentalCondition condition, Set<String> filledSet) {
        String currentSurfacesStr = status.getSurfaces();
        if (currentSurfacesStr == null || currentSurfacesStr.isEmpty()) {
            return;
        }

        String[] currentSurfaces = currentSurfacesStr.split(",");
        List<String> remaining = new ArrayList<>();
        for (String s : currentSurfaces) {
            if (!filledSet.contains(s.trim())) {
                remaining.add(s.trim());
            }
        }

        if (remaining.isEmpty()) {
            deleteStatusSync(patientId, toothNumber, condition);
        } else {
            String remainingStr = android.text.TextUtils.join(",", remaining);
            status.setSurfaces(remainingStr);
            update(status);
        }
    }
}
