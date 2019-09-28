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

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.ion.IonValue;

import software.amazon.qldb.QldbSession;
import software.amazon.qldb.Result;
import software.amazon.qldb.TransactionExecutor;
import software.amazon.qldb.tutorial.model.SampleData;
import software.amazon.qldb.tutorial.model.Cars;

/**
 * Query a table's history for a particular set of documents.
 *
 * This code expects that you have AWS credentials setup per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public final class QueryCarHistory {
    public static final Logger log = LoggerFactory.getLogger(QueryCarHistory.class);
    private static final int THREE_MONTHS = 90;

    private QueryCarHistory() { }

    /**
     * In this example, query the 'VehicleRegistration' history table to find all previous primary owners for a VIN.
     *
     * @param txn
     *              The {@link TransactionExecutor} for lambda execute.
     * @param vin
     *              VIN to find previous primary owners for.
     * @param query
     *              The query to find previous primary owners.
     * @throws IllegalStateException if failed to convert document ID to an {@link IonValue}.
     */
    public static void previousCarOwners(final TransactionExecutor txn, final String carId, final String query) {
        try {
            final String docId = Cars.getDocumentIdByCarId(txn, carId);

            final List<IonValue> parameters = Collections.singletonList(Constants.MAPPER.writeValueAsIonValue(docId));
            log.info("Querying the 'Cars' table's history using Car Id: {}...", carId);
            log.info(query);
            final Result result = txn.execute(query, parameters);
            ScanTable.printDocuments(result);
        } catch (IOException ioe) {
            throw new IllegalStateException(ioe);
        }
    }

    public static void main(final String... args) {
        try (QldbSession qldbSession = ConnectToLedger.createQldbSession()) {
            final String threeMonthsAgo = Instant.now().minus(THREE_MONTHS, ChronoUnit.DAYS).toString();
            final String query = String.format("SELECT data.Owner, data.CarId, data.Model, metadata.version "
                    + "FROM history(Cars, `%s`) "
                    + "AS h WHERE h.metadata.id = ?", threeMonthsAgo);            
            qldbSession.execute(txn -> {
                final String carId = SampleData.CARS.get(0).getCarId();
                previousCarOwners(txn, carId, query);
            }, (retryAttempt) -> log.info("Retrying due to OCC conflict..."));
            log.info("Successfully queried history.");
        } catch (Exception e) {
            log.error("Unable to query history to find previous owners.", e);
        }
    }
}
