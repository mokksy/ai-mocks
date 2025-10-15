# How to Release to Maven Central

1. Delete git tag, if needed:

   ```shell
   git tag -d v0.2.0
   ```
2. Perform the release

   ```shell
   export GPG_TTY=$(tty)
   SONATYPE_USERNAME=...
   SONATYPE_PASSWORD=...
   
   ./gradlew clean build sourcesJar check publishToMavenCentral \
    --stacktrace --rerun-tasks --warning-mode=all \
    -PmavenCentralUsername="$SONATYPE_USERNAME" \
    -PmavenCentralPassword="$SONATYPE_PASSWORD"
   ```

   https://stackoverflow.com/a/57591830/3315474

   In case of GPG error `gpg: signing failed: Screen or window too small`, [try this](https://stackoverflow.com/a/67498543/3315474):

   ```shell
   export GPG_TTY=$(tty)
   gpgconf --kill gpg-agent
   gpg -K --keyid-format SHORT
            
   # Test
   echo "test" | gpg --clearsign
   ```

3. Push

   ```shell
   git push origin 
   git push origin --tags
   ```
