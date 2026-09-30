package com.demo2.docker.legacy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/legacy")
@Tag(name = "Legacy Db2 SQL Analysis & Migration Gap Analysis",
        description = "Demonstrates legacy Db2 SQL query comprehension and mapping to modern Spring Boot REST APIs.")
@CrossOrigin(origins = "*")
public class LegacyCmQueryController {

    private static final Logger log = LoggerFactory.getLogger(LegacyCmQueryController.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LegacyCmQueryController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Executes the moderately complex Db2 query used by legacy IBM Content Manager applications
     * to join item metadata catalog and document storage tables.
     */
    public static final String LEGACY_DB2_SQL = """
            SELECT 
                itm.ITEMID,
                itm.VERSIONID,
                typ.ITEMTYPENAME,
                itm.CREATEDTIMESTAMP,
                itm.ACLCODE,
                doc.FILENAME,
                doc.MIMETYPE,
                doc.DOC_SIZE
            FROM ICMSTITEMS001001 itm
            JOIN ICMSTITEMTYPEDEFS typ ON itm.ITEMTYPEID = typ.ITEMTYPEID
            JOIN ICMSTCOLLNAME001001 doc ON itm.ITEMID = doc.ITEMID
            WHERE itm.ITEMID = ?
            """;

    @Operation(
            summary = "Trace Legacy IBM Content Manager Db2 SQL Query",
            description = "Traces the legacy SQL join across ICMSTITEMS001001, ICMSTITEMTYPEDEFS, and ICMSTCOLLNAME001001, " +
                    "providing side-by-side gap analysis between direct Db2 queries and the target REST API."
    )
    @GetMapping("/trace-sql")
    public ResponseEntity<Map<String, Object>> traceLegacyDb2Query(
            @Parameter(description = "IBM Content Manager Item ID (e.g. ICM_ITEM_001 or ICM_ITEM_002)", example = "ICM_ITEM_001")
            @RequestParam(name = "itemId", defaultValue = "ICM_ITEM_001") String itemId
    ) {
        log.info("Executing legacy Db2 SQL trace for itemId='{}'", itemId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("targetItemId", itemId);
        response.put("legacyDb2Query", LEGACY_DB2_SQL);

        Map<String, String> tableAnalysis = new LinkedHashMap<>();
        tableAnalysis.put("ICMSTITEMS001001", "IBM Content Manager core item catalog table. Contains system attributes: ITEMID, VERSIONID, ITEMTYPEID, CREATEDTIMESTAMP, ACLCODE.");
        tableAnalysis.put("ICMSTITEMTYPEDEFS", "Item type definition table. Maps internal ITEMTYPEID integer to human-readable item type names (e.g., POLICY_DOCUMENT, CLAIM_RECORD).");
        tableAnalysis.put("ICMSTCOLLNAME001001", "Resource collection storage table. Stores document physical properties (FILENAME, MIMETYPE, DOC_SIZE) and binary payload (DOCUMENT_DATA).");
        response.put("db2TableSchemaAnalysis", tableAnalysis);

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(LEGACY_DB2_SQL, itemId);
            response.put("db2QueryExecuted", true);
            response.put("recordsFound", rows.size());
            response.put("db2ResultRows", rows);
        } catch (Exception e) {
            log.warn("Direct query failed (tables might be uninitialized yet): {}", e.getMessage());
            response.put("db2QueryExecuted", false);
            response.put("error", e.getMessage());
        }

        Map<String, String> migrationMapping = new LinkedHashMap<>();
        migrationMapping.put("itm.ITEMID -> DocumentEntity.itemId", "Maps legacy IBM CM 26-char Item ID to modern REST DTO 'itemId'");
        migrationMapping.put("typ.ITEMTYPENAME -> DocumentEntity.itemType", "Maps CM Item Type to REST DTO 'itemType'");
        migrationMapping.put("doc.FILENAME -> DocumentEntity.fileName", "Maps resource filename to REST DTO 'fileName'");
        migrationMapping.put("doc.MIMETYPE -> DocumentEntity.mimeType", "Maps resource MIME to Content-Type header and DTO 'mimeType'");
        migrationMapping.put("doc.DOC_SIZE -> DocumentEntity.fileSize", "Maps byte length to Content-Length header and DTO 'fileSize'");
        migrationMapping.put("doc.DOCUMENT_DATA -> GET /api/v1/documents/{id}/content", "Replaced direct binary LOB retrieval with chunked HTTP streaming (InputStream/OutputStream)");
        response.put("migrationGapAnalysis", migrationMapping);

        return ResponseEntity.ok(response);
    }
}
