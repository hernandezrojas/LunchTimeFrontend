package com.herroj.android.lunchtimefrontend.app;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * Created by Roberto Hernandez on 13/10/2016.
 */

public class FullTestSuite extends TestSuite {

    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class)
                .build();
//        .includeAllPackagesUnderHere().build();

    }

    public FullTestSuite() {
        super();
    }

}
