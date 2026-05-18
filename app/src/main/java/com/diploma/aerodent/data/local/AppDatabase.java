package com.diploma.aerodent.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.diploma.aerodent.data.local.dao.AppointmentDao;
import com.diploma.aerodent.data.local.dao.PatientDao;
import com.diploma.aerodent.data.local.dao.PaymentDao;
import com.diploma.aerodent.data.local.dao.PhotoDao;
import com.diploma.aerodent.data.local.dao.ToothStatusDao;
import com.diploma.aerodent.data.local.dao.ProcedureLogDao;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.Payment;
import com.diploma.aerodent.data.local.entity.Photo;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.entity.ProcedureLog;


@Database(
        entities = {
                Patient.class,
                Appointment.class,
                ProcedureLog.class,
                Payment.class,
                Photo.class,
                ToothStatus.class
        },
        version = 7,
        exportSchema = true
)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {

    public abstract PatientDao patientDao();
    public abstract AppointmentDao appointmentDao();
    public abstract ProcedureLogDao procedureLogDao();
    public abstract PaymentDao paymentDao();
    public abstract PhotoDao photoDao();
    public abstract ToothStatusDao toothStatusDao();

    private static final int NUMBER_OF_THREADS = 4;
    public static final java.util.concurrent.ExecutorService databaseWriteExecutor =
            java.util.concurrent.Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "aerodent_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Load sample data
    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
            super.onOpen(db);

            databaseWriteExecutor.execute(() -> {
                SampleDataGenerator.seedDatabase(INSTANCE);
            });
        }
    };
}
