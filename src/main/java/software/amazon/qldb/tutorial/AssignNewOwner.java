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

package software.amazon.qldb.tutorial;

import com.amazon.ion.IonReader;
import com.amazon.ion.IonStruct;
import com.amazon.ion.IonValue;
import com.amazon.ion.system.IonReaderBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.qldb.QldbSession;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;
import software.amazon.qldb.tutorial.model.SampleData;

/**
 * Find primary owner for a particular vehicle's VIN.
 * Transfer to another primary owner for a particular vehicle's VIN.
 *
 * This code expects that you have AWS credentials setup per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public final class AssignNewOwner {
    public static final Logger log = LoggerFactory.getLogger(AssignNewOwner.class);

    private AssignNewOwner() { }
    /**
     * Update the primary owner for a vehicle registration with the given documentId.
     *
     * @param txn
     *              The {@link TransactionExecutor} for lambda execute.
     * @param vin
     *              Unique VIN for a vehicle.
     * @param documentId
     *              New PersonId for the primary owner.
     * @throws IllegalStateException if no vehicle registration was found using the given document ID and VIN, or if failed
     * to convert parameters into {@link IonValue}.
     */
    public static void assignNewOwner(final TransactionExecutor txn, final String carId, final String owner) {
        try {
            log.info("Updating primary owner for vehicle with Car ID: {}...", carId);
            final String query = "UPDATE Cars AS c SET c.Owner = ? WHERE c.CarId = ?";
            // final String query = "UPDATE Cars AS c SET c.Owner = 'Teja' WHERE c.CarId = 'IN001'";

            final List<IonValue> parameters = new ArrayList<>();
            parameters.add(Constants.MAPPER.writeValueAsIonValue(owner));
            parameters.add(Constants.MAPPER.writeValueAsIonValue(carId));

            Result result = txn.execute(query, parameters);
            // Result result = txn.execute(query);

            ScanTable.printDocuments(result);
            if (result.isEmpty()) {
                throw new IllegalStateException("Unable to transfer vehicle, could not find registration.");
            } else {
                log.info("Successfully transferred vehicle with Car Id '{}' to new owner.", carId);
            }
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    public static void main(final String... args) {
        final String carId = SampleData.CARS.get(0).getCarId();        

        try (QldbSession qldbSession = ConnectToLedger.createQldbSession()) {
            qldbSession.execute(txn -> {                
                assignNewOwner(txn, carId, "Teja");
            }, (retryAttempt) -> log.info("Retrying due to OCC conflict..."));
            log.info("Successfully transferred vehicle ownership!");
        } catch (Exception e) {
            log.error("Error updating Cars: ", e);
        }
    }
}
