package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {

    private Integer diaSemana;

    public DiaSemanaMatcher(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    @Override
    protected boolean matchesSafely(Date date) {
        return DataUtils.verificarDiaSemana(date, this.diaSemana);
    }

    @Override
    public void describeTo(Description description) {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.DAY_OF_WEEK, this.diaSemana);
        String dataPorExtenso = instance.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
        description.appendText(dataPorExtenso);
    }
}
