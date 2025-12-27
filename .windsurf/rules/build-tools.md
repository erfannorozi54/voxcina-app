---
trigger: always_on
---
# Build Tools

## Environment Variables (Required)

```bash
export JAVA_HOME=/opt/android-studio/jbr
export ANDROID_HOME=~/Android/Sdk
export PATH=$JAVA_HOME/bin:$PATH
```

## Build Commands

```bash
# Build
export JAVA_HOME=/opt/android-studio/jbr && export ANDROID_HOME=~/Android/Sdk && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew assembleDebug

# Test
export JAVA_HOME=/opt/android-studio/jbr && export ANDROID_HOME=~/Android/Sdk && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew testDebugUnitTest

# Clean
export JAVA_HOME=/opt/android-studio/jbr && export ANDROID_HOME=~/Android/Sdk && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew clean
```

## Rules

1. Always prefix Gradle commands with the environment exports above
2. Use `./gradlew` (wrapper), not system Gradle
3. Use `| tail -50` to limit verbose output
4. Prefer `getDiagnostics` tool for quick syntax checks before full builds
