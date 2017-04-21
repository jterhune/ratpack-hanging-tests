import org.cassandraunit.CQLDataLoader
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.handling.HandlerDecorator
import ratpack.impose.ImpositionsSpec
import ratpack.impose.ServerConfigImposition
import ratpack.impose.UserRegistryImposition
import ratpack.registry.Registry
import ratpack.test.ApplicationUnderTest
import ratpack.test.remote.RemoteControl
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class HangingTest extends Specification {

	@AutoCleanup
	@Shared
	ApplicationUnderTest application

	RemoteControl remoteControl = new RemoteControl(application)

	void setupSpec() {
		EmbeddedCassandraServerHelper.startEmbeddedCassandra('test-cassandra.yaml', 'build/cassandra-unit',30000)
		CQLDataLoader dataLoader = new CQLDataLoader(EmbeddedCassandraServerHelper.getSession())
		dataLoader.load(new ClassPathCQLDataSet('test-dataset.cql'))

		application = new GroovyRatpackMainApplicationUnderTest() {
			@Override
			protected void addImpositions(ImpositionsSpec impositions) {
				impositions.add(UserRegistryImposition.of {
					Registry.of {
						it.add(HandlerDecorator, ratpack.remote.RemoteControl.handlerDecorator())
					}
				}).add(ServerConfigImposition.of({
					it.props([
							'cassandra.seeds' : ['127.0.0.1']
					])
				}))
			}
		}
	}

	def testOk() {
		expect:
		application.httpClient.get('ok').statusCode == 200

		cleanup:
		remoteControl.exec({
			println 'cleanup'
		})
	}

}
