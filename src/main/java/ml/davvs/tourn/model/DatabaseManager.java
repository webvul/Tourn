package ml.davvs.tourn.model;

import java.util.ArrayList;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;

public class DatabaseManager {
	private Cluster cluster;
	private Session session;
	
	public void connect(String node) {
	   cluster = Cluster.builder()
	         .addContactPoint(node)
	         .build();
	   Metadata metadata = cluster.getMetadata();
	   System.out.printf("Connected to cluster: %s\n",
	         metadata.getClusterName());
	   for ( Host host : metadata.getAllHosts() ) {
		   System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
				   host.getDatacenter(), host.getAddress(), host.getRack());
	   }
	   session = cluster.connect();
	   setupDB();
	}
	
	public void setupDB() {
		session.execute("DROP KEYSPACE if exists tourn");
		
		session.execute("CREATE KEYSPACE IF NOT EXISTS tourn WITH replication "+
				      "= {'class':'SimpleStrategy', 'replication_factor':1};");

		session.execute(
			      "CREATE TABLE IF NOT EXISTS tourn.tournament (" +
			            "id uuid PRIMARY KEY," +
			            "name text," +
			            "seasons list<int>," +
			            ");");
	}
	
	public void close() {
		cluster.close();
	}
}
