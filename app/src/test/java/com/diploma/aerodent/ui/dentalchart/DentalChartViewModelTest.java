package com.diploma.aerodent.ui.dentalchart;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.model.DentalCondition;
import com.diploma.aerodent.data.repository.PatientRepository;
import com.diploma.aerodent.data.repository.ProcedureLogRepository;
import com.diploma.aerodent.data.repository.ToothStatusRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class DentalChartViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application mockApplication;
    @Mock
    private ToothStatusRepository mockToothRepo;
    @Mock
    private PatientRepository mockPatientRepo;
    @Mock
    private ProcedureLogRepository mockLogRepo;
    @Mock
    private Context mockContext;

    private DentalChartViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new DentalChartViewModel(mockApplication, mockToothRepo, mockPatientRepo, mockLogRepo);

        when(mockContext.getString(anyInt())).thenReturn("Conflict");
    }

    @Test
    public void testCheckMissingToothConflict() {
        List<ToothStatus> statuses = new ArrayList<>();
        ToothStatus missingStatus = new ToothStatus();
        missingStatus.setCondition(DentalCondition.MISSING);
        statuses.add(missingStatus);

        // Add Caries to a missing tooth
        String conflict = viewModel.checkConditionConflict(statuses, DentalCondition.CARIES, mockContext);

        assertNotNull(conflict);
        assertEquals("Conflict", conflict);
    }

    @Test
    public void testCheckImplantToothConflict() {
        List<ToothStatus> statuses = new ArrayList<>();
        ToothStatus implantStatus = new ToothStatus();
        implantStatus.setCondition(DentalCondition.IMPLANT);
        statuses.add(implantStatus);

        // Add Periodontitis to an implant
        String conflict = viewModel.checkConditionConflict(statuses, DentalCondition.PERIODONTITIS, mockContext);

        assertNotNull(conflict);
        assertEquals("Conflict", conflict);
    }

    @Test
    public void testCheckRootCanalAndPulpitisConflict() {
        List<ToothStatus> statuses = new ArrayList<>();
        ToothStatus rootCanalStatus = new ToothStatus();
        rootCanalStatus.setCondition(DentalCondition.ROOT_CANAL);
        statuses.add(rootCanalStatus);

        // Add Pulpitis to a tooth that already has a Root Canal
        String conflict = viewModel.checkConditionConflict(statuses, DentalCondition.PULPITIS, mockContext);

        assertNotNull(conflict);
        assertEquals("Conflict", conflict);
    }

    @Test
    public void testCheckConditionWithNoConflict() {
        List<ToothStatus> statuses = new ArrayList<>();
        ToothStatus cariesStatus = new ToothStatus();
        cariesStatus.setCondition(DentalCondition.CARIES);
        statuses.add(cariesStatus);

        // Add Calculus to a tooth that has Caries - should pass
        String conflict = viewModel.checkConditionConflict(statuses, DentalCondition.CALCULUS, mockContext);

        assertNull(conflict);
    }

    @Test
    public void testGetExistingToothSurfaces() {
        List<ToothStatus> statuses = new ArrayList<>();
        ToothStatus status = new ToothStatus();
        status.setCondition(DentalCondition.CARIES);
        status.setSurfaces("M,O,D");
        statuses.add(status);

        List<String> surfaces = viewModel.getExistingSurfaces(statuses, DentalCondition.CARIES);

        assertEquals(3, surfaces.size());
        assertTrue(surfaces.contains("M"));
        assertTrue(surfaces.contains("O"));
        assertTrue(surfaces.contains("D"));
    }

    @Test
    public void testGetVisibleDentalCategories() {
        List<DentalCondition.Category> categories = viewModel.getVisibleCategories();

        assertFalse(categories.contains(DentalCondition.Category.GENERAL));
        assertFalse(categories.isEmpty());
    }

    @Test
    public void testGroupedDentalConditions() {
        Map<DentalCondition.Category, List<DentalCondition>> grouped = viewModel.getGroupedConditions();

        boolean hasHealthy = false;
        for (List<DentalCondition> list : grouped.values()) {
            if (list.contains(DentalCondition.HEALTHY)) {
                hasHealthy = true;
                break;
            }
        }

        assertFalse(hasHealthy);
        assertFalse(grouped.isEmpty());
    }
}
