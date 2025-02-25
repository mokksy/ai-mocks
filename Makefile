.PHONY: build test lint format all # always run

build:
	./gradlew clean build dokkaJavadocJar sourcesJar koverXmlReport

test:
	./gradlew check

apidocs:
	./gradlew clean dokkaGenerate dokkaHtmlMultiModule && \
	mkdir -p build/docs && \
	cp -R mokksy/build/dokka/html build/docs/api

lint:prepare
	ktlint "!**/generated-sources/**" && \
  ./gradlew detekt spotlessCheck

# https://docs.openrewrite.org/recipes/maven/bestpractices
format:prepare
	./gradlew spotlessApply
	./gradlew rewriteRun
	ktlint --format "!**/generated-sources/**"

prepare:
	command -v ktlint >/dev/null 2>&1 || brew install ktlint --quiet

all: format lint build

publish:
	rm -rf ~/.m2/repository/me/kpavlov/aimocks  ~/.m2/repository/me/kpavlov/mokksy
	./gradlew clean build sourcesJar check publishToMavenLocal
	echo "Publishing 📢"
	## https://vanniktech.github.io/gradle-maven-publish-plugin/central/#configuring-maven-central
	# ./gradlew publishToMavenCentral \
	# -PmavenCentralUsername="$SONATYPE_USERNAME" \
  # -PmavenCentralPassword="$SONATYPE_PASSWORD"
