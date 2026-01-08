
# api-test-kit

Reusable Java library for HTTPS API testing using Rest Assured, with TLS/SSL + mTLS support.

## Quick Start (5 minutes)

### 1) Add dependency
Build from source or publish to internal Nexus/Artifactory.

### 2) Create config
Create `src/main/resources/application-qa.yml`:

```yaml
environment: qa
baseUrl: "https://your-qa-domain.example.com"
tls:
  mode: "DEFAULT"
