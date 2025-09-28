# Clean Architecture Structure

## 📁 **Final Project Structure**

```
app/src/main/java/com/sap/codelab/
├── data/                           # Data Layer
│   ├── local/                     # Local data sources
│   │   ├── MemoDao.kt            # Room DAO interface
│   │   └── MemoDatabase.kt       # Room database
│   ├── mapper/                    # Data mapping
│   │   └── MemoMapper.kt         # Domain ↔ Data mapping
│   ├── model/                     # Data models
│   │   └── Memo.kt               # Room entity
│   └── repository/                # Repository implementations
│       └── MemoRepositoryImpl.kt # Repository implementation
│
├── domain/                        # Domain Layer (Business Logic)
│   ├── entity/                   # Domain entities
│   │   └── MemoEntity.kt         # Pure business entity
│   ├── repository/                # Repository interfaces
│   │   └── MemoRepository.kt     # Repository contract
│   ├── usecase/                   # Use cases (Business logic)
│   │   ├── GetAllMemosUseCase.kt
│   │   ├── GetMemoByIdUseCase.kt
│   │   ├── GetOpenMemosUseCase.kt
│   │   ├── MarkMemoAsDoneUseCase.kt
│   │   └── SaveMemoUseCase.kt
│   ├── error/                     # Error handling
│   │   └── AppError.kt           # Error types
│   ├── result/                    # Result types
│   │   ├── Result.kt             # Result sealed class
│   │   └── ResultExtensions.kt   # Result extensions
│   └── validation/                # Input validation
│       └── MemoValidator.kt      # Validation logic
│
├── presentation/                  # Presentation Layer
│   ├── viewmodel/                 # ViewModels
│   │   ├── CreateMemoViewModel.kt
│   │   ├── HomeViewModel.kt
│   │   └── ViewMemoViewModel.kt
│   ├── view/                      # UI Layer (Activities/Fragments)
│   │   ├── create/               # Create memo screen
│   │   │   └── CreateMemo.kt
│   │   ├── detail/               # View memo screen
│   │   │   └── ViewMemo.kt
│   │   ├── home/                 # Home screen
│   │   │   ├── Home.kt
│   │   │   ├── MemoAdapter.kt
│   │   │   └── MemoViewHolder.kt
│   │   └── location/             # Location picker
│   │       └── LocationPickerActivity.kt
│   └── utils/                     # Presentation utilities
│       ├── ConnectivityManager.kt # Network monitoring
│       ├── ErrorHandler.kt       # Error handling
│       └── Extensions.kt         # Extension functions
│
├── di/                           # Dependency Injection
│   ├── App.kt                    # Application class
│   └── SimpleDIContainer.kt      # DI container
│
├── service/                      # Background Services
│   ├── LocationService.kt        # Location monitoring
│   ├── NotificationActionReceiver.kt # Notification actions
│   ├── NotificationHelper.kt     # Notification management
│   └── ServiceManager.kt         # Service management
│
│
└── utils/                        # Shared Utilities
    ├── coroutines/
    │   └── ScopeProvider.kt      # Coroutine scopes
    ├── DebugHelper.kt            # Debug utilities
    └── extensions/               # Extension functions
        ├── ActivityExtensions.kt
        └── StringExtensions.kt
```

## 🏗️ **Clean Architecture Layers**

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

### **Scalability**
- Easy to add new features
- Clear patterns for new developers
- Modular architecture

### **Maintainability**
- Changes in one layer don't affect others
- Clear dependencies flow inward
- Easy to refactor individual components

## 🔄 **Dependency Flow**

```
UI Layer → Presentation Layer → Domain Layer ← Data Layer
    ↓              ↓              ↓              ↓
Activities    ViewModels    Use Cases    Repositories
Fragments     UI State     Entities     Data Sources
```

**Dependencies flow inward only:**
- UI depends on Presentation
- Presentation depends on Domain
- Data depends on Domain
- Domain depends on nothing (pure business logic)

## ✅ **Clean Architecture Principles Applied**

1. **Dependency Inversion**: High-level modules don't depend on low-level modules
2. **Single Responsibility**: Each class has one reason to change
3. **Open/Closed**: Open for extension, closed for modification
4. **Interface Segregation**: Clients depend only on interfaces they use
5. **Liskov Substitution**: Derived classes must be substitutable for base classes
