package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataDiferencaDiasMatcher extends TypeSafeMatcher<Date> {

    private Integer quantidadeDias;

    public DataDiferencaDiasMatcher(Integer quantidadeDias) {
        this.quantidadeDias = quantidadeDias;
    }

    @Override
    protected boolean matchesSafely(Date date) {
        return DataUtils.isMesmaData(date, DataUtils.obterDataComDiferencaDias(this.quantidadeDias));
    }

    @Override
    public void describeTo(Description description) {
        Date dataEsperada = DataUtils.obterDataComDiferencaDias(this.quantidadeDias);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        description.appendText(format.format(dataEsperada));
    }
}
