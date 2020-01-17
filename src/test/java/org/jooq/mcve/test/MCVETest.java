/*
 * This work is dual-licensed
 * - under the Apache Software License 2.0 (the "ASL")
 * - under the jOOQ License and Maintenance Agreement (the "jOOQ License")
 * =============================================================================
 * You may choose which license applies to you:
 *
 * - If you're using this work with Open Source databases, you may choose
 *   either ASL or jOOQ License.
 * - If you're using this work with at least one commercial database, you must
 *   choose jOOQ License
 *
 * For more information, please visit http://www.jooq.org/licenses
 *
 * Apache Software License 2.0:
 * -----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * jOOQ License and Maintenance Agreement:
 * -----------------------------------------------------------------------------
 * Data Geekery grants the Customer the non-exclusive, timely limited and
 * non-transferable license to install and use the Software under the terms of
 * the jOOQ License and Maintenance Agreement.
 *
 * This library is distributed with a LIMITED WARRANTY. See the jOOQ License
 * and Maintenance Agreement for more details: http://www.jooq.org/licensing
 */
package org.jooq.mcve.test;

import static org.jooq.mcve.Tables.APPROVALS;
import static org.jooq.mcve.Tables.ORDER_LINES;
import static org.jooq.mcve.Tables.REVIEWERS;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MCVETest {

    Connection connection;
    DSLContext ctx;

    @Before
    public void setup() throws Exception {
        connection = DriverManager.getConnection("jdbc:postgresql:mcve", "sa", "");
        ctx = DSL.using(connection);
    }

    @After
    public void after() throws Exception {
        ctx = null;
        connection.close();
        connection = null;
    }

    @Test
    public void mcveTest() {

        Result<Record2<Integer, Integer>> result = ctx.select(ORDER_LINES.ID, REVIEWERS.ID)
                .from(ORDER_LINES)
                .join(REVIEWERS)
                .on(REVIEWERS.DEPARTMENT_NAME.eq(ORDER_LINES.orders().DEPARTMENT_NAME))
                .where(
                        DSL.notExists(
                                DSL.selectOne()
                                        .from(APPROVALS)
                                        .where(APPROVALS.ORDER_LINE_ID.eq(ORDER_LINES.ID), APPROVALS.REVIEWER_ID.eq(REVIEWERS.ID))
                        )
                )
                .fetch();

        assertEquals(Integer.valueOf(1), result.getValue(0, ORDER_LINES.ID));
    }
}
