package example;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.harness.Neo4jBuilders;
import org.neo4j.harness.Neo4j;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JoinTest {

    private static final Config driverConfig = Config.build().withoutEncryption().toConfig();
    private Neo4j embeddedDatabaseServer;

    @BeforeAll
    void initializeNeo4j() {

        this.embeddedDatabaseServer = Neo4jBuilders
                .newInProcessBuilder()
                .withFunction( Join.class )
                .build();
    }

    @Test
    public void shouldAllowIndexingAndFindingANode() {

        // This is in a try-block, to make sure we close the driver after the test
        try( Driver driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), driverConfig);
             Session session = driver.session()) {

            // When
            String result = session.run( "RETURN example.join(['Hello', 'World']) AS result").single().get("result").asString();

            // Then
            assertThat( result).isEqualTo(( "Hello,World" ));
        }
    }
}