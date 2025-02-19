.PHONY: build test lint format all # always run

build:
	./gradlew clean build koverXmlReport

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
