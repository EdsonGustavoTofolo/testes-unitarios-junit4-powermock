package br.ce.wcaquino.servicos.matchers;

import java.util.Calendar;

public class OwnMatchers {

    public static DiaSemanaMatcher caiEm(Integer diaSemana) {
        return new DiaSemanaMatcher(diaSemana);
    }

    public static DiaSemanaMatcher caiNumaSegundaFeira() {
        return new DiaSemanaMatcher(Calendar.MONDAY);
    }

    public static DataDiferencaDiasMatcher ehHojeComDiferencaDias(Integer quantidadeDias) {
        return new DataDiferencaDiasMatcher(quantidadeDias);
    }

    public static DataDiferencaDiasMatcher ehHoje() {
        return new DataDiferencaDiasMatcher(0);
    }
}
