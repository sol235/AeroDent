package com.diploma.aerodent.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.Calendar;
import java.util.Date;

import com.diploma.aerodent.data.local.dao.AppointmentDao;
import com.diploma.aerodent.data.local.dao.PatientDao;
import com.diploma.aerodent.data.local.dao.PaymentDao;
import com.diploma.aerodent.data.local.dao.PhotoDao;
import com.diploma.aerodent.data.local.dao.TreatmentDao;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.Payment;
import com.diploma.aerodent.data.local.entity.Photo;
import com.diploma.aerodent.data.local.entity.Treatment;


@Database(
        entities = {
                Patient.class,
                Appointment.class,
                Treatment.class,
                Payment.class,
                Photo.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {

    public abstract PatientDao patientDao();
    public abstract AppointmentDao appointmentDao();
    public abstract TreatmentDao treatmentDao();
    public abstract PaymentDao paymentDao();
    public abstract PhotoDao photoDao();

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
                            .fallbackToDestructiveMigration(false)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // test data
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                PatientDao patientDao = INSTANCE.patientDao();
                AppointmentDao appointmentDao = INSTANCE.appointmentDao();

                // Add patients
                Patient p1 = new Patient();
                p1.setFirstName("Georgi");
                p1.setLastName("Ivanov");
                p1.setPhoneNumber("08884352");
                long p1Id = patientDao.insert(p1);

                Patient p2 = new Patient();
                p2.setFirstName("John");
                p2.setLastName("Georgiev");
                p2.setPhoneNumber("089432435");
                long p2Id = patientDao.insert(p2);

                // Add appointments
                Calendar cal = Calendar.getInstance();
                
                cal.set(Calendar.HOUR_OF_DAY, 9);
                cal.set(Calendar.MINUTE, 0);
                Appointment a1 = new Appointment();
                a1.setPatientId((int) p1Id);
                a1.setDateTime(cal.getTime());
                a1.setTreatmentType("Преглед");
                a1.setStatus("Дошъл");
                appointmentDao.insert(a1);

                cal.set(Calendar.HOUR_OF_DAY, 11);
                cal.set(Calendar.MINUTE, 30);
                Appointment a2 = new Appointment();
                a2.setPatientId((int) p2Id);
                a2.setDateTime(cal.getTime());
                a2.setTreatmentType("Почистване");
                a2.setStatus("Предстои");
                appointmentDao.insert(a2);
            });
        }
    };
}
