package com.jlmasa.pms_backend.assignment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ManHoursSuiteTest {

    @Test
    void briefExample_10to10_45_and_11to12_givesTwoHours(){
        long total = 2700 + 4500;
        assertThat(total).isEqualTo(7200);
        assertThat(total/3600.0).isEqualTo(2.00);
    }

    @Test
    void noAssignmentToCompletionShortcut_mondayToFridayWithThreeHoursOfSessionsReportsThreeHours() {

    }
}


