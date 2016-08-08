package ru.mos.polls.db;

import com.annotatedsql.annotation.provider.Provider;
import com.annotatedsql.annotation.provider.URI;
import com.annotatedsql.annotation.sql.Column;
import com.annotatedsql.annotation.sql.Column.Type;
import com.annotatedsql.annotation.sql.Columns;
import com.annotatedsql.annotation.sql.From;
import com.annotatedsql.annotation.sql.Join;
import com.annotatedsql.annotation.sql.PrimaryKey;
import com.annotatedsql.annotation.sql.Schema;
import com.annotatedsql.annotation.sql.SimpleView;
import com.annotatedsql.annotation.sql.Table;

@Schema(className = "PollsSchema", dbName = "polls_data.db", dbVersion = 3)
@Provider(name = "PollsProvider", authority = "ru.mos.polls.AUTHORITY", schemaClass = "PollsSchema", openHelperClass = "PollsHelper")
public interface PollsData {

    @Table(TPolls.TABLE_NAME)
    public static interface TPolls {

        @URI
        String URI_CONTENT = "polls";

        String TABLE_NAME = "polls";

        @PrimaryKey
        @Column(type = Type.INTEGER)
        String ID = "_id";

        @Column(type = Type.INTEGER)
        String POLL_ID = "poll_id";

        @Column(type = Type.TEXT)
        String TITLE = "title";

        @Column(type = Type.INTEGER)
        String POINTS = "points";

        @Column(type = Type.INTEGER)
        String STATUS = "status";

        @Column(type = Type.TEXT)
        String AUTHOR = "author";

        @Column(type = Type.INTEGER)
        String QUEST_COUNT = "quest_count";

        @Column(type = Type.INTEGER)
        String GROUP_ID = "group_id";

        @Column(type = Type.TEXT)
        String MARK = "mark";
    }


    @Table(TPollGroups.TABLE_NAME)
    public static interface TPollGroups {

        @URI
        String URI_CONTENT = "pollgroups";

        String TABLE_NAME = "poll_groups";

        @PrimaryKey
        @Column(type = Type.INTEGER)
        String ID = "_id";

        @Column(type = Type.INTEGER)
        String GROUP_ID = "group_id";

        @Column(type = Type.TEXT)
        String TITLE = "title";

    }

    @SimpleView(TPollGroupView.VIEW_NAME)
    public static interface TPollGroupView {

        @URI(type = URI.Type.DIR, onlyQuery = true)
        String URI_CONTENT = "view_group";

        String VIEW_NAME = "view_group";

        @From(TPolls.TABLE_NAME)
        @Columns(value = {TPolls.POLL_ID, TPolls.TITLE, TPolls.POINTS, TPolls.STATUS, TPolls.AUTHOR, TPolls.MARK, TPolls.QUEST_COUNT})
        String POLLS = "polls";

        @Join(joinTable = TPollGroups.TABLE_NAME, joinColumn = TPollGroups.GROUP_ID, onTableAlias = POLLS, onColumn = TPolls.GROUP_ID)
        @Columns(value = {TPollGroups.GROUP_ID, TPollGroups.TITLE})
        String GR = "gr";
    }

    @Table(TQuestions.TABLE_NAME)
    public static interface TQuestions {

        @URI
        String URI_CONTENT = "questions";

        String TABLE_NAME = "questions";

        @PrimaryKey
        @Column(type = Type.INTEGER)
        String ID = "_id";

        @Column(type = Type.INTEGER)
        String QUESTION_ID = "question_id";

        @Column(type = Type.TEXT)
        String QUESTION = "question";

        @Column(type = Type.INTEGER)
        String TYPE = "type";

        @Column(type = Type.INTEGER)
        String POLL_ID = "poll_id";
    }


    @Table(TVariants.TABLE_NAME)
    public static interface TVariants {

        @URI
        String URI_CONTENT = "variants";

        String TABLE_NAME = "variants";

        @PrimaryKey
        @Column(type = Type.INTEGER)
        String ID = "_id";

        @Column(type = Type.INTEGER)
        String VARIANT_ID = "variant_id";

        @Column(type = Type.INTEGER)
        String KIND = "kind";

        @Column(type = Type.TEXT)
        String TEXT = "text";

        @Column(type = Type.TEXT)
        String RIGHT_TEXT = "rightText";

        @Column(type = Type.INTEGER)
        String INPUT_TYPE = "input_type";

        @Column(type = Type.TEXT)
        String MIN = "min";

        @Column(type = Type.TEXT)
        String MAX = "max";
    }

    @Table(TPointHistory.TABLE_NAME)
    public static interface TPointHistory {
        @URI
        String URI_CONTENT = "point_history";

        String TABLE_NAME = "point_history";

        @PrimaryKey
        @Column(type = Type.INTEGER)
        String ID = "_id";

        @Column(type = Type.TEXT)
        String TITLE = "title";

        @Column(type = Type.INTEGER)
        String WRITE_OFF_DATE = "write_off_date";

        @Column(type = Type.INTEGER)
        String POINTS = "points";

        @Column(type = Type.INTEGER)
        String REFILL = "refill";

        @Column(type = Type.TEXT)
        String ACTION = "action";
    }

}
