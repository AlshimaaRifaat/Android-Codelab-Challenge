# Clean Architecture Structure

## 🏗️ **Technologies Used**

### **Core Technologies:**
- **Kotlin**: Primary programming language
- **Android Jetpack**: Modern Android development toolkit
- **Room Database**: Local data persistence
- **Coroutines**: Asynchronous programming
- **StateFlow**: Reactive state management
- **Material Design**: UI components and theming

### **Architecture Patterns:**
- **Clean Architecture**: Layered architecture with dependency inversion
- **MVVM (Model-View-ViewModel)**: Presentation layer pattern
- **Repository Pattern**: Data access abstraction
- **Use Case Pattern**: Business logic encapsulation
- **Dependency Injection**: Manual DI container

### **Testing Framework:**
- **JUnit 4**: Unit testing framework
- **Kotlin Coroutines Test**: Coroutine testing utilities
- **AndroidX Test**: Android testing libraries
- **Mock Repository**: Test doubles for isolated testing

##  **Clean Architecture Layers**

### **1. Domain Layer** (Business Logic)
- **Entities**: Pure business objects (`MemoEntity`)
- **Use Cases**: Business logic operations
- **Repository Interfaces**: Contracts for data access
- **Error Handling**: Business error types
- **Validation**: Input validation logic

### **2. Data Layer** (Data Sources)
- **Local Data**: Room database, SharedPreferences
- **Remote Data**: API calls, network requests
- **Repository Implementation**: Concrete implementations
- **Mappers**: Data transformation between layers

### **3. Presentation Layer** (UI Logic)
- **ViewModels**: UI state management
- **UI State**: Loading, success, error states
- **User Interactions**: Button clicks, form inputs
- **Navigation**: Screen transitions

### **4. Dependency Injection**
- **DI Container**: Manual dependency injection
- **Application Setup**: Initialization
- **Service Locator**: Dependency resolution

## 🎯 **Benefits of This Structure**

### **Separation of Concerns**
- Each layer has a single responsibility
- Clear boundaries between layers
- Easy to understand and maintain

### **Testability**
- Domain layer is pure Kotlin (no Android dependencies)
- Use cases can be unit tested easily
- Repository interfaces allow for mocking
- **Comprehensive Unit Testing**: 22 unit tests covering all ViewModels
- **Test Coverage**: Initial states, validation logic, error handling
- **Isolated Testing**: Mock repositories for dependency-free testing

### **Scalability**
- Easy to add new features
- Clear patterns for new developers
- Modular architecture

### **Maintainability**
- Changes in one layer don't affect others
- Clear dependencies flow inward
- Easy to refactor individual components

**Dependencies flow inward only:**
- UI depends on Presentation
- Presentation depends on Domain
- Data depends on Domain
- Domain depends on nothing (pure business logic)

## 🧪 **Unit Testing Implementation**

### **Test Structure:**
- **3 Separate Test Classes**: One for each ViewModel
- **Mock Repositories**: Isolated testing without external dependencies
- **Coroutine Testing**: Proper async testing with `StandardTestDispatcher`


### **Testing Technologies:**
- **JUnit 4**: Core testing framework
- **Kotlin Coroutines Test**: Async testing utilities
- **AndroidX Test**: Android-specific testing libraries
- **Mock Repository Pattern**: Test doubles for isolated testing

## **Clean Architecture Principles Applied**

1. **Dependency Inversion**: High-level modules don't depend on low-level modules
2. **Single Responsibility**: Each class has one reason to change
3. **Open/Closed**: Open for extension, closed for modification
4. **Interface Segregation**: Clients depend only on interfaces they use
5. **Liskov Substitution**: Derived classes must be substitutable for base classes
