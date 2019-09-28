/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package software.amazon.qldb.tutorial.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import software.amazon.qldb.TransactionExecutor;
import software.amazon.qldb.tutorial.Constants;

/**
 * Represents a vehicle registration, serializable to (and from) Ion.
 */ 
public final class Cars {
    private final String carId;
    private final String manfacturer;
    private final String model;
    private final String owner;        

    @JsonCreator
    public Cars(@JsonProperty("CarId") final String carId,
                @JsonProperty("Manfacture") final String manfacturer,
                @JsonProperty("Model") final String model,
                @JsonProperty("Owner") final String owner) {
        this.carId = carId;
        this.manfacturer = manfacturer;
        this.model = model;
        this.owner = owner;        
    }

    @JsonProperty("CarId")
    public String getCarId() {
        return carId;
    }

    @JsonProperty("Manfacturer")
    public String getManfacture() {
        return manfacturer;
    }

    @JsonProperty("Model")
    public String getModel() {
        return model;
    }

    @JsonProperty("Owner")
    public String getOwner() {
        return owner;
    }

    /**
     * Returns the unique document ID of a vehicle given a specific Car ID.
     *
     * @param txn
     *              A transaction executor object.
     * @param carId
     *              The Car ID of a vehicle.
     * @return the unique document ID of the specified vehicle.
     */
    public static String getDocumentIdByCarId(final TransactionExecutor txn, final String carId) {
        return SampleData.getDocumentId(txn, Constants.CARS_TABLE_NAME, "CarId", carId);
    }
}
