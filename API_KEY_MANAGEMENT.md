# API Key Management - Production Guide

## 🔐 **Secure API Key Management Implementation**

This project now uses a production-ready approach for managing Google Maps API keys that follows security best practices.

## 📋 **Current Implementation**

### **1. Build Configuration**
- API key is injected at build time using `buildConfigField`
- Supports both environment variables and local.properties
- No hardcoded keys in source code

### **2. Development Setup**
```properties
# local.properties (for development only)
GOOGLE_MAPS_API_KEY=your_api_key_here
```

### **3. Production Setup**
```bash
# Set environment variable
export GOOGLE_MAPS_API_KEY=your_production_api_key_here
```

## 🚀 **Deployment Strategies**

### **Option 1: Environment Variables (Recommended)**
```bash
# CI/CD Pipeline
export GOOGLE_MAPS_API_KEY=your_production_key
./gradlew assembleRelease
```

### **Option 2: CI/CD Secrets**
```yaml
# GitHub Actions example
env:
  GOOGLE_MAPS_API_KEY: ${{ secrets.GOOGLE_MAPS_API_KEY }}
```

### **Option 3: Build Variants**
```gradle
// app/build.gradle
buildTypes {
    debug {
        buildConfigField "String", "GOOGLE_MAPS_API_KEY", "\"${getDebugApiKey()}\""
    }
    release {
        buildConfigField "String", "GOOGLE_MAPS_API_KEY", "\"${getReleaseApiKey()}\""
    }
}
```

## 🛡️ **Security Best Practices**

### **1. Key Restrictions**
- **Android App Restrictions**: Restrict API key to your app's package name and SHA-1 fingerprint
- **HTTP Referrer Restrictions**: For web usage
- **IP Address Restrictions**: For server-to-server usage

### **2. Key Rotation**
- Rotate API keys regularly (every 90 days)
- Use different keys for development, staging, and production
- Monitor API usage and set quotas

### **3. Access Control**
- Limit API key access to necessary team members only
- Use least privilege principle
- Monitor and audit API key usage

## 📁 **File Structure**

```
project/
├── app/
│   ├── build.gradle          # Build configuration with API key injection
│   └── src/main/
│       └── AndroidManifest.xml  # Uses ${GOOGLE_MAPS_API_KEY}
├── local.properties          # Development API key (gitignored)
└── .env                      # Environment variables (gitignored)
```

## 🔧 **Configuration Examples**

### **Development Environment**
```properties
# local.properties
GOOGLE_MAPS_API_KEY=AIzaSyATVlbLXDs9PATbSOf521FhMqNqnXyyUko
```

### **Production Environment**
```bash
# Environment variable
export GOOGLE_MAPS_API_KEY=AIzaSyB_Production_Key_Here
```

### **CI/CD Pipeline**
```yaml
# .github/workflows/build.yml
- name: Build APK
  env:
    GOOGLE_MAPS_API_KEY: ${{ secrets.GOOGLE_MAPS_API_KEY }}
  run: ./gradlew assembleRelease
```

## 🚨 **Security Checklist**

### **Before Production Deployment**
- [ ] **API Key Restrictions**: Configure in Google Cloud Console
- [ ] **Environment Variables**: Set in production environment
- [ ] **Remove local.properties**: Ensure it's not committed to version control
- [ ] **Monitor Usage**: Set up API usage monitoring
- [ ] **Backup Keys**: Store production keys securely
- [ ] **Team Access**: Limit access to necessary team members

### **Ongoing Security**
- [ ] **Regular Rotation**: Rotate keys every 90 days
- [ ] **Usage Monitoring**: Monitor for unusual activity
- [ ] **Access Reviews**: Regular access reviews
- [ ] **Incident Response**: Plan for key compromise

## 🔍 **Troubleshooting**

### **Build Errors**
```bash
# Error: GOOGLE_MAPS_API_KEY not found
# Solution: Set environment variable or add to local.properties
export GOOGLE_MAPS_API_KEY=your_key_here
```

### **Runtime Errors**
```bash
# Error: Maps not loading
# Check: API key restrictions in Google Cloud Console
# Verify: Package name and SHA-1 fingerprint
```

## 📊 **Monitoring and Alerts**

### **API Usage Monitoring**
- Set up Google Cloud Console monitoring
- Configure alerts for unusual usage
- Monitor quota limits

### **Security Alerts**
- Monitor for unauthorized access
- Set up alerts for key rotation
- Track API key usage patterns

## 🎯 **Production Deployment Steps**

1. **Generate Production API Key**
   - Create new API key in Google Cloud Console
   - Configure restrictions (Android app, package name, SHA-1)

2. **Set Environment Variable**
   ```bash
   export GOOGLE_MAPS_API_KEY=your_production_key
   ```

3. **Build Release APK**
   ```bash
   ./gradlew assembleRelease
   ```

4. **Verify Configuration**
   - Test maps functionality
   - Verify API key restrictions
   - Monitor usage

## 🔒 **Additional Security Measures**

### **ProGuard/R8 Obfuscation**
```proguard
# Keep API key references
-keep class com.sap.codelab.BuildConfig { *; }
```

### **Runtime Validation**
```kotlin
// Validate API key at runtime
if (BuildConfig.GOOGLE_MAPS_API_KEY.isNullOrEmpty()) {
    throw SecurityException("API key not configured")
}
```

### **Key Encryption**
```kotlin
// For additional security, encrypt API key
fun getEncryptedApiKey(): String {
    return decrypt(BuildConfig.GOOGLE_MAPS_API_KEY)
}
```

## **Summary**

This implementation provides:
- **Secure API key management**
- **Environment-based configuration**
- **Production-ready deployment**
- **Security best practices**
- **Easy maintenance and rotation**


