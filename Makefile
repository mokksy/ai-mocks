build:
	  mvn clean verify dokka:dokka site

test:
	  mvn clean verify

apidocs:
	  mvn clean compile dokka:dokka -pl !reports && \
    mkdir -p target/docs && \
		cp -R core/target/dokka target/docs/api

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
