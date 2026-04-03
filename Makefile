.PHONY: build
build:
	./gradlew checkLegacyAbi build && \
	./gradlew koverHtmlReport koverVerify publishToMavenLocal && \
	(cd ai-mocks-openai/samples/shadow && mvn test)

.PHONY: clean
clean:
	@echo "🧽 Cleaning..."
	@./gradlew clean

.PHONY: apidump
apidump:
	@echo "🪏 API dump..."
	@./gradlew updateLegacyAbi

.PHONY: test
test:
	./gradlew check koverXmlReport koverVerify koverLog

.PHONY: apidocs
apidocs:
	rm -rf docs/public/apidocs && \
	./gradlew clean :docs:dokkaGenerate

.PHONY: knit
knit:
	@echo "🪡🧶 Running Knit..."
	@rm -rf docs/build
	@./gradlew knit :docs:test
	@echo "✅ Knit completed!"

.PHONY: lint
lint:
	@./gradlew detekt

# https://docs.openrewrite.org/recipes/maven/bestpractices
.PHONY: format
format:
	@./gradlew detekt --auto-correct

.PHONY: all
all: format lint build knit apidocs

.PHONY: pom
pom:
	@./gradlew generatePomFileForKotlinMultiplatformPublication

.PHONY: publish
publish:
	rm -rf ~/.m2/repository/me/kpavlov/aimocks  ~/.m2/repository/me/kpavlov/mokksy
	./gradlew --rerun-tasks clean build check sourcesJar publishToMavenLocal
	echo "Publishing 📢"
	## https://vanniktech.github.io/gradle-maven-publish-plugin/central/#configuring-maven-central
	# ./gradlew publishToMavenCentral \
	# -PmavenCentralUsername="$SONATYPE_USERNAME" \
  # -PmavenCentralPassword="$SONATYPE_PASSWORD"
