package javaposse.jobdsl.dsl

import spock.lang.Specification

import static org.codehaus.groovy.runtime.InvokerHelper.createScript

class AbstractJobManagementSpec extends Specification {
    def 'deprecation warning in DSL script'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        GroovyClassLoader classLoader = new GroovyClassLoader()
        Class scriptClass = classLoader.parseClass(this.class.getResourceAsStream('/deprecation.groovy').text)
        Script script = createScript(scriptClass, new Binding([jm: jobManagement]))

        when:
        script.run()

        then:
        buffer.toString().trim() == 'Warning: testMethod is deprecated (DSL script, line 1)'
    }

    def 'deprecation warning in source file'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        URL[] roots = [this.class.getResource('/deprecation.groovy')]
        GroovyScriptEngine groovyScriptEngine = new GroovyScriptEngine(roots)

        when:
        groovyScriptEngine.run('deprecation.groovy', new Binding([jm: jobManagement]))

        then:
        buffer.toString().trim() == 'Warning: testMethod is deprecated (deprecation.groovy, line 1)'
    }

    def 'deprecation warning with custom subject in DSL script'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        GroovyClassLoader classLoader = new GroovyClassLoader()
        Class scriptClass = classLoader.parseClass(this.class.getResourceAsStream('/deprecation-subject.groovy').text)
        Script script = createScript(scriptClass, new Binding([jm: jobManagement]))

        when:
        script.run()

        then:
        buffer.toString().trim() == 'Warning: foo is deprecated (DSL script, line 3)'
    }

    def 'deprecation warning with custom subject  in source file'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        URL[] roots = [this.class.getResource('/deprecation-subject.groovy')]
        GroovyScriptEngine groovyScriptEngine = new GroovyScriptEngine(roots)

        when:
        groovyScriptEngine.run('deprecation-subject.groovy', new Binding([jm: jobManagement]))

        then:
        buffer.toString().trim() == 'Warning: foo is deprecated (deprecation-subject.groovy, line 3)'
    }

    def 'custom deprecation warning in DSL script'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        when:
        jobManagement.logDeprecationWarning('foo', 'script123123123.groovy', 12)

        then:
        buffer.toString().trim() == 'Warning: foo is deprecated (DSL script, line 12)'
    }

    def 'custom deprecation warning in source file'() {
        setup:
        ByteArrayOutputStream buffer = new ByteArrayOutputStream()
        AbstractJobManagement jobManagement = new TestJobManagement(new PrintStream(buffer))

        when:
        jobManagement.logDeprecationWarning('foo', 'test.groovy', 12)

        then:
        buffer.toString().trim() == 'Warning: foo is deprecated (test.groovy, line 12)'
    }

    static class TestJobManagement extends MockJobManagement {
        TestJobManagement() {
            super()
        }

        TestJobManagement(PrintStream out) {
            super(out)
        }

        @Override
        String getConfig(String jobName) {
            throw new UnsupportedOperationException()
        }

        @Override
        boolean createOrUpdateConfig(String jobName, String config, boolean ignoreExisting) {
            throw new UnsupportedOperationException()
        }

        @Override
        void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
            throw new UnsupportedOperationException()
        }

        @Override
        InputStream streamFileInWorkspace(String filePath) throws IOException {
            throw new UnsupportedOperationException()
        }

        @Override
        String readFileInWorkspace(String filePath) throws IOException {
            throw new UnsupportedOperationException()
        }

        void testMethod() {
            logDeprecationWarning()
        }

        void testMethodWithCustomSubject() {
            logDeprecationWarning('foo')
        }
    }
}
