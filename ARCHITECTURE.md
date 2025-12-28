# Architecture Overview

## Project Structure

```
CheckTracker (KMP Project)
│
├── Shared Module (Common + Platform-Specific)
│   ├── commonMain ──────────────────────┐
│   │   ├── data/                        │
│   │   │   ├── model/                   │
│   │   │   │   └── Expense.kt          │ Shared across
│   │   │   └── repository/              │ Android & iOS
│   │   ├── domain/                      │
│   │   │   ├── ocr/                     │
│   │   │   └── parser/                  │
│   │   └── presentation/                │
│   │       ├── ui/                      │
│   │       ├── viewmodel/               │
│   │       └── navigation/              │
│   │                                     │
│   ├── androidMain ──────────────────────────┐
│   │   └── OcrProcessor (ML Kit)            │ Platform-specific
│   │                                         │ implementations
│   ├── iosMain ────────────────────────┐
│   │   └── OcrProcessor (Vision)        │
│   │                                    │
│   └── commonTest ──────────────────────┐
│       ├── ReceiptParserTest            │ Unit tests
│       ├── ExpenseRepositoryTest        │
│       └── TestData                     │
│
├── Android App
│   ├── MainActivity.kt
│   ├── AndroidManifest.xml
│   └── res/
│
└── iOS App
    └── (Swift project structure)
```

## Architectural Patterns

### MVVM (Model-View-ViewModel)

```
┌─────────────────────────────────────────┐
│         Presentation Layer (UI)         │
│  DashboardScreen, CameraScreen          │
│  AnalyticsScreen, HistoryScreen         │
└──────────────────┬──────────────────────┘
                   │ observes
                   ↓
┌─────────────────────────────────────────┐
│        ViewModel Layer (State)          │
│  DashboardViewModel                     │
│  CameraViewModel                        │
│  - Manages UI state                     │
│  - Handles user interactions            │
│  - Updates via StateFlow<State>        │
└──────────────────┬──────────────────────┘
                   │ uses
                   ↓
┌─────────────────────────────────────────┐
│        Domain Layer (Business Logic)    │
│  ReceiptParser.parseReceipt()           │
│  OcrProcessor.recognizeText()           │
│  - Parsing logic                        │
│  - Validation                           │
│  - OCR operations                       │
└──────────────────┬──────────────────────┘
                   │ calls
                   ↓
┌─────────────────────────────────────────┐
│   Data/Repository Layer (Persistence)   │
│  ExpenseRepository                      │
│  SQLDelight Database                    │
│  - CRUD operations                      │
│  - Filtering & queries                  │
│  - Data transformation                  │
└─────────────────────────────────────────┘
```

### Repository Pattern

```
┌─────────────────┐
│   ViewModel     │
└────────┬────────┘
         │ asks for data
         ↓
┌──────────────────────────────────────┐
│   ExpenseRepository (Interface)      │
│  - addExpense()                      │
│  - updateExpense()                   │
│  - getExpensesByDateRange()          │
│  - getMonthlySum()                   │
└──────────┬───────────────────────────┘
           │ implements
           ↓
┌──────────────────────────────────────┐
│  InMemoryExpenseRepository           │ (Testing)
│  or                                  │
│  SqlDelightExpenseRepository         │ (Production)
│  - Actual data operations            │
│  - Database queries                  │
└──────────────────────────────────────┘
```

## Data Flow

### Receipt Scanning Flow

```
1. User taps "Сканировать"
   ↓
2. CameraScreen shows camera preview
   ↓
3. CameraViewModel.processReceiptText(rawOCRText)
   ↓
4. ReceiptParser.parseReceipt(text)
   │   ├── extractAmount() → Regex match "ИТОГО: XXX ₽"
   │   ├── extractDate() → Regex match "DD.MM.YYYY"
   │   ├── extractMerchant() → Look for known stores
   │   └── determineCategory() → Match keywords
   ↓
5. ParsedReceipt returned with:
   - amount: 1250.00
   - date: 2025-12-28
   - merchant: "Магнит"
   - category: "Еда"
   - confidence: 0.95
   ↓
6. ConfirmationScreen displays parsed data
   ↓
7. User confirms or edits fields
   ↓
8. CameraViewModel.saveExpense()
   │   ├── Create Expense object
   │   └── repository.addExpense()
   ↓
9. Data saved to local database
   ↓
10. DashboardScreen updates (StateFlow)
    - Monthly sum updated
    - Category breakdown recalculated
```

### Analytics Data Flow

```
DashboardViewModel.loadDashboard()
   ↓
repository.getAllExpenses()
   ↓
SQLDelight returns Flow<List<Expense>>
   ↓
Calculate aggregations:
├── monthlySum = filter by month + sum
├── dailySum = filter by date + sum
└── categoryStats = groupBy + sum per category
   ↓
Update state:
_state.value = DashboardState(
    expenses = [...],
    monthlySum = 45000.0,
    categoryStats = {FOOD: 25000, TRANSPORT: 15000, ...}
)
   ↓
UI re-renders (Compose observation)
```

## expect/actual for Platform Code

```
commonMain/
  └── domain/ocr/OcrProcessor.kt
      │
      ├── expect class OcrProcessor {
      │      suspend fun recognizeText(): String
      │      fun release()
      │   }
      │
      ├── androidMain/
      │   └── OcrProcessor.kt
      │       ├── actual class OcrProcessor {
      │       │   - Uses ML Kit
      │       │   - TextRecognition.getClient()
      │       │   - RussianTextRecognizerOptions
      │       └── }
      │
      └── iosMain/
          └── OcrProcessor.kt
              ├── actual class OcrProcessor {
              │   - Uses Vision Framework
              │   - VNRecognizeTextRequest
              │   - Language: ru-RU, en-US
              └── }
```

## State Management (Compose)

```
┌─────────────────────────────────────┐
│  DashboardState (Data Class)        │
│  ├── expenses: List<Expense>        │
│  ├── monthlySum: Double             │
│  ├── categoryStats: Map<...>        │
│  ├── isLoading: Boolean             │
│  └── selectedDate: LocalDate        │
└──────────────┬──────────────────────┘
               │
               ↓
┌──────────────────────────────────────┐
│  StateFlow<DashboardState>           │
│  - Emits state changes               │
│  - Cold stream (created on subscribe)│
└──────────────┬──────────────────────┘
               │
               ↓
┌──────────────────────────────────────┐
│  DashboardScreen (Composable)        │
│  - Collects latest state             │
│  - Recomposes on state change        │
│  - Efficient re-rendering            │
└──────────────────────────────────────┘
```

## Testing Architecture

```
┌────────────────────────────────────────┐
│  ReceiptParserTest                     │
├────────────────────────────────────────┤
│ ✓ testExtractAmount_ValidFormat       │
│ ✓ testExtractAmount_WithComma         │
│ ✓ testExtractDate_StandardFormat      │
│ ✓ testRecognizeMerchant_Magnit        │
│ ✓ testCategoryDetection_Transport     │
│ ✓ testConfidenceCalculation           │
│ ✓ testFullReceiptParsing_RealExample  │
│ ✓ testEmptyReceipt                    │
│ ✓ testCaseInsensitivity               │
│         ... 11 tests total            │
└────────────────────────────────────────┘

┌────────────────────────────────────────┐
│  ExpenseRepositoryTest                 │
├────────────────────────────────────────┤
│ ✓ testAddExpense                       │
│ ✓ testGetAllExpenses                   │
│ ✓ testUpdateExpense                    │
│ ✓ testDeleteExpense                    │
│ ✓ testGetExpensesByDateRange           │
│ ✓ testGetMonthlySum                    │
│ ✓ testGetDailySum                      │
│ ✓ testIncomeNotCounted                 │
│         ... 9 tests total              │
└────────────────────────────────────────┘
```

## Regex Parsing Strategy

```
Input: Receipt OCR text
   ↓
Step 1: Amount Extraction
   Regex: (?i)(?:итого|общая сумма|ко платежу)[:\s]+([0-9]{1,}[.,][0-9]{2})\s*₽
   Extract: "1250.00" or "1250,00"
   Normalize: Replace comma with dot
   Result: 1250.00
   ↓
Step 2: Date Extraction
   Regex: (\d{1,2})[-./](\d{1,2})[-./](\d{4})
   Extract: Day, Month, Year
   Validate: Day 1-31, Month 1-12
   Result: LocalDate(2025, 12, 28)
   ↓
Step 3: Merchant Detection
   For each known merchant:
      If text contains "магнит" (case-insensitive)
         Result: "Магнит"
   Fallback: First non-empty line
   ↓
Step 4: Category Determination
   Check merchant → category map
   OR
   Check keywords in text against category keywords
   Fallback: ExpenseCategory.OTHER
   ↓
Output: ParsedReceipt(amount, date, merchant, category, confidence)
```

## Performance Optimization

1. **OCR**: Process only within frame boundaries (not full image)
2. **Database**: Batch operations (insert 5-10 expenses together)
3. **UI**: LazyColumn for long lists, debounc search input (300ms)
4. **Cache**: Image cache limit 50MB
5. **Coroutines**: Main thread for UI, Default for background tasks

---

**Architecture Version**: 1.0.0  
**Pattern**: KMP + MVVM + Repository  
**State Management**: Kotlin Flow + StateFlow  
**Testing Strategy**: Unit tests >80% coverage
