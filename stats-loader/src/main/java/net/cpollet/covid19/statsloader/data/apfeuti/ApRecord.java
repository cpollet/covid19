/*
 * Copyright 2020 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.covid19.statsloader.data.apfeuti;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

/**
 * Holds one data point.
 * <p>
 * See <a href="https://github.com/openZH/covid_19">https://github.com/openZH/covid_19</a>.
 */
@Data
public class ApRecord {
    /**
     * Date of notification.
     */
    @JsonProperty(value = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;

    /**
     * Time of notification.
     */
    @JsonProperty(value = "time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    private LocalTime time;

    /**
     * Abbreviation of the reporting canton.
     */
    @JsonProperty(value = "abbreviation_canton_and_fl")
    private String place;

    /**
     * Reported number of tests performed as of date.
     * <p>
     * Irrespective of canton of residence.
     */
    @JsonProperty(value = "ncumul_tested")
    private Integer cumulatedTested;

    /**
     * Reported number of confirmed cases as of date.
     * <p>
     * Only cases that reside in the current canton.
     */
    @JsonProperty(value = "ncumul_conf")
    private Integer cumulatedConfirmed;

    /**
     * New hospitalisations since last date.
     * <p>
     * Irrespective of canton of residence.
     */
    @JsonProperty(value = "new_hosp")
    private Integer newHospitalised;

    /**
     * Reported number of hospitalised patients on date.
     * <p>
     * Irrespective of canton of residence.
     */
    @JsonProperty(value = "current_hosp")
    private Integer currentHospitalised;

    /**
     * Reported number of hospitalised patients in ICUs on date.
     * <p>
     * Irrespective of canton of residence.
     */
    @JsonProperty(value = "current_icu")
    private Integer currentIcu;

    /**
     * Reported number of patients requiring invasive ventilation on date.
     * <p>
     * Irrespective of canton of residence.
     */
    @JsonProperty(value = "current_vent")
    private Integer currentVentilated;

    /**
     * Reported number of patients released from hospitals or reported recovered as of date.
     * <p>
     * Irrespective of canton of residence.
     */
    @JsonProperty(value = "ncumul_released")
    private Integer cumulatedReleased;

    /**
     * Reported number of deceased as of date.
     * <p>
     * Only cases that reside in the current canton.
     */
    @JsonProperty(value = "ncumul_deceased")
    private Integer cumulatedDeceased;

    /**
     * Source of the information.
     */
    @JsonProperty(value = "source")
    private String source;

    @JsonProperty(value = "ncumul_tested_fwd")
    private int cumulatedTestedForward;

    @JsonProperty(value = "ncumul_conf_fwd")
    private int cumulatedConfirmedForward;

    /**
     * @deprecated use currentHospitalised
     */
    @Deprecated
    @JsonProperty(value = "ncumul_hosp")
    private Integer cumulatedHospitalised;

    /**
     * @deprecated use currentIcu
     */
    @Deprecated
    @JsonProperty(value = "ncumul_ICU")
    private Integer cumulatedIcu;

    /**
     * @deprecated use currentVentilated
     */
    @Deprecated
    @JsonProperty(value = "ncumul_vent")
    private Integer cumulatedVentilated;

    @JsonProperty(value = "ncumul_hosp_fwd")
    private int cumulatedHospitalisedForward;

    @JsonProperty(value = "ncumul_ICU_fwd")
    private int cumulatedIcuForward;

    @JsonProperty(value = "ncumul_vent_fwd")
    private int cumulatedVentilatedForward;

    @JsonProperty(value = "current_hosp_fwd")
    private int currentHospitalisedForward;

    @JsonProperty(value = "current_icu_fwd")
    private int currentIcuForward;

    @JsonProperty(value = "current_vent_fwd")
    private int currentVentilatedForward;

    @JsonProperty(value = "ncumul_released_fwd")
    private int cumulatedReleasedForward;

    @JsonProperty(value = "ncumul_deceased_fwd")
    private int cumulatedDeceasedForward;

    /**
     * Reported number of isolated persons on date.
     * <p>
     * Infected persons, who are not hospitalised.
     */
    @JsonProperty(value = "current_isolated")
    private Integer currentIsolated;

    /**
     * Reported number of quarantined persons on date.
     * <p>
     * Persons, who were in 'close contact' with an infected person, while that person was infectious, and are not
     * hospitalised themselves.
     */
    @JsonProperty(value = "current_quarantined")
    private Integer currentQuarantined;

    /**
     * undocumented.
     */
    @JsonProperty(value = "current_quarantined_riskareatravel")
    private Integer currentQuarantinedRiskAreaTravel;

    /**
     * undocumented.
     */
    @JsonProperty(value = "current_quarantined_total")
    private Integer currentQuarantinedTotal;

    /**
     * undocumented.
     */
    @JsonProperty(value = "ncumul_confirmed_non_resident")
    private Integer cumulatedConfirmedNonResident;

    /**
     * undocumented.
     */
    @JsonProperty(value = "current_hosp_non_resident")
    private Integer currentHospitalisedNonResident;

    /**
     * undocumented.
     */
    @JsonProperty(value = "current_hosp_resident")
    private Integer currentHospitalisedResident;

    /**
     * undocumented.
     */
    @JsonProperty(value = "ncumul_ICF")
    private Integer cumulatedIcf;

    /**
     * undocumented.
     */
    @JsonProperty(value = "TotalPosTests1")
    private Integer totalPosTests1;

    /**
     * undocumented.
     */
    @JsonProperty(value = "ninst_ICU_intub")
    private Integer ninstIcuIntub;

    public long getTimestamp() {
        LocalDateTime dateTime = LocalDateTime.of(
                getDate(),
                Optional.ofNullable(getTime()).orElse(LocalTime.of(23, 59, 59))
        );
        return dateTime.toEpochSecond(ZoneId.of("Europe/Zurich").getRules().getOffset(dateTime));
    }

    public String getDayOfWeek () {
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }
}
