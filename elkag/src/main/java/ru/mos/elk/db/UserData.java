package ru.mos.elk.db;

import com.annotatedsql.annotation.provider.Provider;
import com.annotatedsql.annotation.provider.URI;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.Column.Type;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.RawQuery;
import com.annotatedsql.annotation.sql.Schema;
import com.annotatedsql.annotation.sql.SqlQuery;
import com.annotatedsql.annotation.sql.Table;


@Schema(className = "UserSchema", dbName = "elk_user_data.db", dbVersion = 5) // в версии 1.8.1 бд версии 3, 1.9 - 4, 1.9.1 - 5
@Provider(name = "UserDataProvider", authority = "ru.mos.elk.AUTHORITY", schemaClass = "UserSchema",openHelperClass="UserDataHelper")
public interface UserData {
    @Table(Cars.TABLE_NAME)
    public static interface Cars {

        @URI
        String URI_CONTENT = "cars";

        String TABLE_NAME = "cars";

        @PrimaryKey
        @Column(type = Column.Type.INTEGER)
        String ID = "_id";

        @Column(type= Column.Type.INTEGER)
        String VEHICLE_ID = "vehicle_id";

        @Column(type= Column.Type.TEXT)
        String CAR_NUMBER = "car_number";

        @Column(type= Column.Type.TEXT)
        String STS_NUMBER = "sts_number";

        @Column(type= Column.Type.INTEGER)
        String IS_DEFAULT = "is_default";
    }

    @Table(Flats.TABLE_NAME)
    public static interface Flats {

        @URI
        String URI_CONTENT = "flats";

        String TABLE_NAME = "flats";

        @PrimaryKey
        @Column(type = Column.Type.INTEGER)
        String ID = "_id";

        @Column(type=Type.TEXT)
        String FLAT_ID = "flat_id";

        @Column(type=Type.TEXT)
        String BUILDING_ID = "building_id";

        @Column(type=Type.TEXT)
        String EPD = "epd";

        @Column(type=Type.TEXT)
        String FLAT = "flat";

        @Column(type=Type.TEXT)
        String BUILDING = "building";

        @Column(type=Type.TEXT)
        String STREET = "street";

        @Column(type=Type.TEXT)
        String ELECTRO_ACCOUNT = "electro_account";

        @Column(type=Type.TEXT)
        String ELECTRO_DEVICE = "electro_device";

        @Column(type=Type.INTEGER)
        String IS_HOTWATER = "hotwater_available";

        @Column(type=Type.INTEGER)
        String IS_DEFAULT = "is_default";

        @Column(type=Type.INTEGER)
        String IS_RESIDENCE = "is_residence";

        @Column(type=Type.INTEGER)
        String IS_REGISTRATION = "is_registration";

        @Column(type=Type.INTEGER)
        String IS_WORK = "is_work";

        @Column(type=Type.INTEGER)
        String EDITING_BLOCKED = "editing_blocked";

        @Column(type=Type.TEXT)
        String CITY = "city";

        @Column(type=Type.TEXT)
        String DISTRICT = "district";

        @Column(type=Type.TEXT)
        String AREA = "area";

        @Column(type=Type.TEXT)
        String AREA_ID = "area_id";
    }

    @Table(Subscriptions.TABLE_NAME)
    public static interface Subscriptions {

        @URI
        String URI_CONTENT = "subscriptions";

        String TABLE_NAME = "subscriptions";

        @PrimaryKey
        @Column(type = Column.Type.INTEGER)
        String ID = "_id";

        @Column(type=Type.TEXT)
        String TYPE = "type";

        @Column(type=Type.INTEGER)
        String EMAIL = "email";

        @Column(type=Type.INTEGER)
        String SMS = "sms";

        @Column(type=Type.INTEGER)
        String PUSH = "push";

    }

    @Table(BlackList.TABLE_NAME)
    public static interface BlackList {

        @URI
        String URI_CONTENT = "blacklist";

        String TABLE_NAME = "blacklist";

        @PrimaryKey
        @Column(type = Column.Type.INTEGER)
        String ID = "_id";

        @Column(type=Type.TEXT)
        String BLOCK_ID = "block_id";

        @Column(type=Type.TEXT)
        String AUTHOR = "author";

        @Column(type=Type.INTEGER)
        String TYPE = "type";
    }

    @Table(Times.TABLE_NAME)
    public static interface Times {

        @URI
        String URI_CONTENT = "times";

        String TABLE_NAME = "times";

        @PrimaryKey
        @Column(type = Column.Type.INTEGER)
        String ID = "_id";

        @Column(type=Type.INTEGER)
        String TIME_ID = "id";

        @Column(type=Type.INTEGER)
        String IS_CHECKED = "is_checked";

        @Column(type=Type.TEXT)
        String NAME = "name";
    }

    @Table(Roads.TABLE_NAME)
    public static interface Roads {

        @URI
        String URI_CONTENT = "roads";

        String TABLE_NAME = "roads";

        @PrimaryKey
        @Column(type = Column.Type.INTEGER)
        String ID = "_id";

        @Column(type = Column.Type.TEXT)
        String  NAME = "name";

        @Column(type = Column.Type.TEXT)
        String IS_CHECKED = "is_checked";

        @Column(type = Column.Type.TEXT)
        String ROAD_ID = "road_id";

        @Column(type = Column.Type.INTEGER)
        String GROUP_ID = "group_id";
    }

    @Table(RoadGroups.TABLE_NAME)
    public static interface RoadGroups {

        @URI
        String URI_CONTENT = "roadgroups";

        String TABLE_NAME = "roadgroups";

        @PrimaryKey
        @Column(type = Column.Type.INTEGER)
        String ID = "_id";

        @Column(type = Column.Type.TEXT)
        String  NAME = "group_name";
    }

    @RawQuery(RoadsFull.QUERY_NAME)
    public static interface RoadsFull {

        @URI(type = URI.Type.DIR, onlyQuery = true)
        String URI_CONTENT = "roads_path";

        String QUERY_NAME = "roads_path";

        @SqlQuery
        String QUERY = "SELECT r."+Roads.ROAD_ID+" as _id, r."+Roads.ROAD_ID+", r."+Roads.NAME+", r."+Roads.IS_CHECKED+", rg."+RoadGroups.NAME+" FROM "+Roads.TABLE_NAME+" as r JOIN "+RoadGroups.TABLE_NAME+" AS rg ON rg._id = r.group_id";
    }



}