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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApTotals {
    /**
     * Reported number of tests performed.
     */
    @JsonProperty(value = "ncumul_tested_fwd")
    private int cumulatedTested;

    /**
     * Reported number of confirmed cases.
     */
    @JsonProperty(value = "ncumul_conf_fwd")
    private int cumulatedConfirmed;

    /**
     * @deprecated use currentHospitalised
     */
    @Deprecated
    @JsonProperty(value = "ncumul_hosp_fwd")
    private int cumulatedHospitalised;

    /**
     * @deprecated use currentIcu
     */
    @Deprecated
    @JsonProperty(value = "ncumul_ICU_fwd")
    private int cumulatedIcu;

    /**
     * @deprecated use currentVentilated
     */
    @Deprecated
    @JsonProperty(value = "ncumul_vent_fwd")
    private int cumulatedVentilated;

    /**
     * Reported number of hospitalised patients.
     */
    @JsonProperty(value = "current_hosp_fwd")
    private int currentHospitalised;

    /**
     * Reported number of hospitalised patients in ICUs.
     */
    @JsonProperty(value = "current_icu_fwd")
    private int currentIcu;

    /**
     * Reported number of patients requiring invasive ventilation.
     */
    @JsonProperty(value = "current_vent_fwd")
    private int currentVentilated;

    /**
     * Reported number of patients released from hospitals or reported recovered.
     */
    @JsonProperty(value = "ncumul_released_fwd")
    private int cumulatedReleased;

    /**
     * Reported number of deceased.
     */
    @JsonProperty(value = "ncumul_deceased_fwd")
    private int cumulatedDeceased;
}
