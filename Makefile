.PHONY: build
build:
	./gradlew clean build dokkaJavadocJar sourcesJar koverHtmlReport

.PHONY: test
test:
	./gradlew check

.PHONY: apidocs
apidocs:
	rm -rf docs/public/apidocs && \
	./gradlew clean :docs:dokkaGenerate

.PHONY: docs
docs:apidocs
	git submodule sync && \
  git submodule update --init --depth=1 && \
	cd docs && \
	hugo server -D --watch

.PHONY: lint
lint:prepare
	ktlint "!**/build/**" && \
  ./gradlew detekt spotlessCheck

# https://docs.openrewrite.org/recipes/maven/bestpractices
.PHONY: format
format:prepare
	./gradlew spotlessApply rewriteRun
	ktlint --format "!**/build/**"

.PHONY: prepare
prepare:
	command -v ktlint >/dev/null 2>&1 || brew install ktlint --quiet

.PHONY: all
all: format lint build

.PHONY: pom
pom:
	./gradlew generatePomFileForKotlinMultiplatformPublication

.PHONY: publish
publish:
	rm -rf ~/.m2/repository/me/kpavlov/aimocks  ~/.m2/repository/me/kpavlov/mokksy
	./gradlew clean build check sourcesJar publishToMavenLocal
	echo "Publishing 📢"
	## https://vanniktech.github.io/gradle-maven-publish-plugin/central/#configuring-maven-central
	# ./gradlew publishToMavenCentral \
	# -PmavenCentralUsername="$SONATYPE_USERNAME" \
  # -PmavenCentralPassword="$SONATYPE_PASSWORD"
