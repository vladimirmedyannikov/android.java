package ru.mos.polls.poll;


import org.junit.Assert;
import org.junit.Test;

import ru.mos.polls.BaseUnitTest;
import ru.mos.polls.R;
import ru.mos.polls.poll.model.Kind;

/**
 * Created by Trunks on 25.04.2017.
 */

public class KindUnitTest extends BaseUnitTest {

    @Test
    public void getKind() {
        Kind standart = Kind.parse("standart");
        Assert.assertEquals(Kind.STANDART, standart);
        Assert.assertEquals(standart.isStandart(), true);
        Assert.assertEquals(standart.getLabel(), "");
        Assert.assertEquals(standart.getColor(), android.R.color.transparent);

        Kind hearingPreview = Kind.parse("hearing_preview");
        Assert.assertEquals(Kind.HEARING_PREVIEW, hearingPreview);
        Assert.assertEquals(hearingPreview.isHearingPreview(), true);
        Assert.assertEquals(hearingPreview.getLabel(), "публичное слушание");
        Assert.assertEquals(hearingPreview.getColor(), R.color.public_poll);

        Kind hearing = Kind.parse("hearing");
        Assert.assertEquals(Kind.HEARING, hearing);
        Assert.assertEquals(hearing.isHearing(), true);
        Assert.assertEquals(hearing.getLabel(), "публичное слушание");
        Assert.assertEquals(hearing.getColor(), R.color.greenText);

        Kind special = Kind.parse("special");
        Assert.assertEquals(Kind.SPECIAL, special);
        Assert.assertEquals(special.isSpecial(), true);
        Assert.assertEquals(special.getLabel(), "специальное голосование");
        Assert.assertEquals(special.getColor(), R.color.special_poll);

        Kind inform = Kind.parse("informer");
        Assert.assertEquals(Kind.INFORM, inform);
        Assert.assertEquals(inform.isInform(), true);
        Assert.assertEquals(inform.getLabel(), "информирование");
        Assert.assertEquals(inform.getColor(), R.color.special_poll);
    }
}
