workspace(name = "oauth2-server")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

# Download master or specific revisions
http_archive(
    name = "io_bazel_rules_kotlin_master",
    sha256 = "08710aa22db195d09ca0cbd0e8f4944bb51f6cfd9f63be743969dee505b2996f",
    strip_prefix = "rules_kotlin-master",
    urls = ["https://github.com/bazelbuild/rules_kotlin/archive/refs/heads/master.zip"],
)

load("@io_bazel_rules_kotlin_master//src/main/starlark/release_archive:repository.bzl", "archive_repository")

archive_repository(
    name = "io_bazel_rules_kotlin",
    source_repository_name = "io_bazel_rules_kotlin_master",
)

load("@io_bazel_rules_kotlin//kotlin:repositories.bzl", "kotlin_repositories", "kotlinc_version", "versions")

kotlin_repositories(
    compiler_release = kotlinc_version(
        release = "1.5.31",
        sha256 = "661111286f3e5ac06aaf3a9403d869d9a96a176b62b141814be626a47249fe9e",
    ),
)

load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kt_register_toolchains")

kt_register_toolchains()

http_archive(
    name = "rules_jvm_external",
    sha256 = versions.RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % versions.RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % versions.RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:repositories.bzl", "rules_jvm_external_deps")

rules_jvm_external_deps()

load("@rules_jvm_external//:setup.bzl", "rules_jvm_external_setup")

rules_jvm_external_setup()

load("@rules_jvm_external//:defs.bzl", "maven_install")

protobuf_version = "3.14.0"

grpc_version = "1.37.1"

MAVEN_REPOSITORIES = [
    "https://jcenter.bintray.com/",
    "https://maven.google.com",
    "https://repo1.maven.org/maven2",
    "https://jitpack.io",
]

load("@rules_jvm_external//:specs.bzl", "maven")

maven_install(
    artifacts = [
        "javax.annotation:javax.annotation-api:1.3.2",
        "com.google.guava:guava:18.0",
        "io.jsonwebtoken:jjwt-api:0.10.5",
        "io.jsonwebtoken:jjwt-impl:0.10.5",
        "io.jsonwebtoken:jjwt-jackson:0.10.5",
        "javax.servlet:servlet-api:2.5",
        "com.clouway.fserve:fserve:0.1.3",
        "com.fasterxml.jackson.core:jackson-core:2.9.0.pr2",
        "com.fasterxml.jackson.core:jackson-annotations:2.9.0.pr2",
        "com.fasterxml.jackson.core:jackson-databind:2.9.0.pr2",
        "com.google.code.gson:gson:2.2.4",
        "com.google.protobuf:protobuf-java:%s" % protobuf_version,
        "com.google.inject:guice:3.0",
        "com.google.inject.extensions:guice-servlet:3.0",
        "ch.qos.logback:logback-classic:0.9.30",
        "org.eclipse.jetty:jetty-server:8.1.8.v20121106",
        "org.eclipse.jetty:jetty-servlet:8.1.8.v20121106",
        "com.google.sitebricks:sitebricks:0.8.8",
        "com.google.sitebricks:sitebricks-annotations:0.8.8",
        "com.google.sitebricks:sitebricks-client:0.8.8",
        "com.google.sitebricks:sitebricks-converter:0.8.8",
        "javax.json:javax.json-api:1.0",
        "net.hamnaberg.json:json-javax:1.0",
        "com.clouway.security:jwt-java-client-okhttp:0.0.1",
        "com.squareup.okhttp3:okhttp:3.6.0",
        maven.artifact(
            group = "com.github.mobiletoly",
            artifact = "urlsome",
            version = "0.4",
            exclusions = [
                "org.jetbrains.kotlin:kotlin-stdlib-jdk8",
            ],
        ),
        # Testin Libraries
        "org.hamcrest:hamcrest-all:1.3",
        "junit:junit:4.11",
        "org.jmock:jmock:2.6.0",
        "org.jmock:jmock-junit4:2.6.0",
        "com.clouway.fserve:testing:0.1.3",
        "nl.jqno.equalsverifier:equalsverifier:2.4",
    ],
    repositories = MAVEN_REPOSITORIES,
)
