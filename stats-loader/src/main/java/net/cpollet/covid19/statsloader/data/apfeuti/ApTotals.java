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

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;

@Data
public class ApTotals {
    /**
     * Reported number of tests performed.
     */
    @XmlAttribute(name = "ncumul_tested_fwd")
    private int cumulatedTested;

    /**
     * Reported number of confirmed cases.
     */
    @XmlAttribute(name = "ncumul_conf_fwd")
    private int cumulatedConfirmed;

    /**
     * @deprecated use currentHospitalised
     */
    @Deprecated
    @XmlAttribute(name = "ncumul_hosp_fwd")
    private int cumulatedHospitalised;

    /**
     * @deprecated use currentIcu
     */
    @Deprecated
    @XmlAttribute(name = "ncumul_ICU_fwd")
    private int cumulatedIcu;

    /**
     * @deprecated use currentVentilated
     */
    @Deprecated
    @XmlAttribute(name = "ncumul_vent_fwd")
    private int cumulatedVentilated;

    /**
     * Reported number of hospitalised patients.
     */
    @XmlAttribute(name = "current_hosp_fwd")
    private int currentHospitalised;

    /**
     * Reported number of hospitalised patients in ICUs.
     */
    @XmlAttribute(name = "current_icu_fwd")
    private int currentIcu;

    /**
     * Reported number of patients requiring invasive ventilation.
     */
    @XmlAttribute(name = "current_vent_fwd")
    private int currentVentilated;

    /**
     * Reported number of patients released from hospitals or reported recovered.
     */
    @XmlAttribute(name = "ncumul_released_fwd")
    private int cumulatedReleased;

    /**
     * Reported number of deceased.
     */
    @XmlAttribute(name = "ncumul_deceased_fwd")
    private int cumulatedDeceased;
}
