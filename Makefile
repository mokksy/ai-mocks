.PHONY: build test lint format all docs # always run

build:
	./gradlew clean build dokkaJavadocJar sourcesJar koverHtmlReport

test:
	./gradlew check

apidocs:
	rm -rf docs/public/apidocs && \
	./gradlew clean :docs:dokkaGenerate

docs:apidocs
	git submodule sync && \
  git submodule update --init --depth=1 && \
	cd docs && \
	hugo server -D --watch

lint:prepare
	ktlint "!**/build/**" && \
  ./gradlew detekt spotlessCheck

# https://docs.openrewrite.org/recipes/maven/bestpractices
format:prepare
	./gradlew spotlessApply rewriteRun
	ktlint --format "!**/build/**"

prepare:
	command -v ktlint >/dev/null 2>&1 || brew install ktlint --quiet

all: format lint build

publish:
	rm -rf ~/.m2/repository/me/kpavlov/aimocks  ~/.m2/repository/me/kpavlov/mokksy
	./gradlew clean build check sourcesJar publishToMavenLocal
	echo "Publishing 📢"
	## https://vanniktech.github.io/gradle-maven-publish-plugin/central/#configuring-maven-central
	# ./gradlew publishToMavenCentral \
	# -PmavenCentralUsername="$SONATYPE_USERNAME" \
  # -PmavenCentralPassword="$SONATYPE_PASSWORD"
