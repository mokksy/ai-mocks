##
build:
	  ./gradlew clean build koverXmlReport

test:
	  ./gradlew check

apidocs:
	  ./gradlew build dokkaHtmlMultiModule

lint:prepare
	  ktlint && \
    mvn spotless:check

# https://docs.openrewrite.org/recipes/maven/bestpractices
format:prepare
	  ktlint --format && \
  	mvn spotless:apply && \
	  mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
				-Drewrite.activeRecipes=org.openrewrite.maven.BestPractices \
				-Drewrite.exportDatatables=true

prepare:
	  brew install ktlint --quiet

all: format lint build
