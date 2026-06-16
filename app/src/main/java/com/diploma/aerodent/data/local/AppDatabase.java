package com.diploma.aerodent.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.diploma.aerodent.data.local.dao.AppointmentDao;
import com.diploma.aerodent.data.local.dao.PatientDao;
import com.diploma.aerodent.data.local.dao.PaymentDao;
import com.diploma.aerodent.data.local.dao.PhotoDao;
import com.diploma.aerodent.data.local.dao.ToothStatusDao;
import com.diploma.aerodent.data.local.dao.ProcedureLogDao;
import com.diploma.aerodent.data.local.dao.UserDao;
import com.diploma.aerodent.data.local.entity.Appointment;
import com.diploma.aerodent.data.local.entity.Patient;
import com.diploma.aerodent.data.local.entity.Payment;
import com.diploma.aerodent.data.local.entity.Photo;
import com.diploma.aerodent.data.local.entity.ToothStatus;
import com.diploma.aerodent.data.local.entity.ProcedureLog;
import com.diploma.aerodent.data.local.entity.User;


@Database(
        entities = {
                Patient.class,
                Appointment.class,
                ProcedureLog.class,
                Payment.class,
                Photo.class,
                ToothStatus.class,
                User.class
        },
        version = 13,
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
    public abstract UserDao userDao();

    private static final int NUMBER_OF_THREADS = 4;
    public static final java.util.concurrent.ExecutorService databaseWriteExecutor =
            java.util.concurrent.Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static final Migration MIGRATION_10_11 = new Migration(10, 11) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1");
        }
    };

    static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE procedure_logs ADD COLUMN userId TEXT");
            database.execSQL("ALTER TABLE procedure_logs ADD COLUMN creatorName TEXT");
            database.execSQL("ALTER TABLE tooth_statuses ADD COLUMN userId TEXT");
            database.execSQL("ALTER TABLE tooth_statuses ADD COLUMN creatorName TEXT");
        }
    };

    private static volatile AppDatabase INSTANCE;
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "aerodent_database")
                            .addMigrations(MIGRATION_10_11, MIGRATION_12_13)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
