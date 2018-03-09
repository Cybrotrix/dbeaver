/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2018 Serge Rider (serge@jkiss.org)
 * Copyright (C) 2017-2018 Alexander Fedorov (alexander.fedorov@jkiss.org)
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
package org.jkiss.dbeaver.ext.postgresql.debug.ui.internal;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.jkiss.dbeaver.debug.core.DebugCore;
import org.jkiss.dbeaver.debug.ui.DatabaseLaunchShortcut;
import org.jkiss.dbeaver.ext.postgresql.debug.core.PostgreSqlDebugCore;
import org.jkiss.dbeaver.ext.postgresql.model.PostgreProcedure;
import org.jkiss.dbeaver.model.struct.DBSObject;

public abstract class PgSqlBaseLaunchShortcut extends DatabaseLaunchShortcut {

    public PgSqlBaseLaunchShortcut() {
        super(PostgreSqlDebugCore.CONFIGURATION_TYPE, PostgreDebugUIMessages.PgSqlLaunchShortcut_name);
    }

    @Override
    protected boolean isCandidate(ILaunchConfiguration config, DBSObject launchable) {
        if (!config.exists()) {
            return false;
        }
        boolean isInstance = launchable instanceof PostgreProcedure;
        if (!isInstance) {
            return false;
        }
        PostgreProcedure procedure = (PostgreProcedure) launchable;

        String datasource = DebugCore.extractDatasourceId(config);
        String id = launchable.getDataSource().getContainer().getId();
        if (!datasource.equals(id)) {
            return false;
        }

        String database = DebugCore.extractDatabaseName(config);
        String databaseName = procedure.getDatabase().getName();
        if (!database.equals(databaseName)) {
            return false;
        }

        String schema = DebugCore.extractSchemaName(config);
        String schemaName = procedure.getContainer().getName();
        if (!schema.equals(schemaName)) {
            return false;
        }

        try {
            String oid = config.getAttribute(DebugCore.ATTR_PROCEDURE_OID, String.valueOf(0));
            long objectId = procedure.getObjectId();
            if (!(Long.parseLong(oid) == objectId)) {
                return false;
            }
        } catch (Exception e) {
            // ignore
            return false;
        }
        return true;
    }

}