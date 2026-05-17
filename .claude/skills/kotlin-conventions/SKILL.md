---
name: kotlin-conventions
description: Pure Kotlin (non-Android) coding conventions for code generation and code review. Use whenever .kt files are written, edited, or reviewed; whenever Kotlin naming, formatting, modifier order, or idioms come up; whenever the user mentions "Kotlin convention", "Kotlin style", "Kotlin naming", "val vs var", "scope functions", "expression body", or any Kotlin-specific style question. Faithful to kotlinlang.org/docs/coding-conventions.html — no team-specific additions.
---

# Kotlin Conventions

Faithful summary of the official style guide. Source: https://kotlinlang.org/docs/coding-conventions.html

**Tradeoff:** Bias toward what the official docs say. Don't invent local rules.

## 1. Naming

**Names follow the language, not the developer's preference.**

- Packages: `lowercase`, no underscores. Multi-word discouraged; if unavoidable, concatenate or `camelCase`.
- Classes / objects: `UpperCamelCase`.
- Functions / properties / locals: `lowerCamelCase`.
- Constants (`const val`, top-level / object `val` of deeply immutable data): `SCREAMING_SNAKE_CASE`.
- Top-level / object `val` holding behavior or mutable data: `camelCase`.
- `val` holding a singleton: same as `object` (`UpperCamelCase`).
- Enum constants: `SCREAMING_SNAKE_CASE` or `UpperCamelCase` — pick per use.
- Backing property: `_camelCase` (private), exposed as `camelCase`.
- Acronyms: 2 letters → all caps (`IOStream`); 3+ → first letter only (`XmlFormatter`, `HttpInputStream`).
- Factory function with same name as the abstract type it returns is allowed (`fun Foo(): Foo`).
- Tests only: backticked method names with spaces, or underscores, are allowed.

**Semantic naming.**

- Class = noun or noun phrase (`List`, `PersonReader`).
- Function = verb or verb phrase (`close`, `readPersons`).
- Mutation vs copy: `sort` (in place) vs `sorted` (returns copy). Match this pattern.
- Avoid meaningless words: `Manager`, `Wrapper`, `Util`.

## 2. Source Organization

**File name = what's in the file.**

- Single class/interface → file name matches the class (`Foo.kt`).
- Multiple declarations or top-level only → name describing contents in `UpperCamelCase` (`ProcessDeclarations.kt`).
- Multiplatform: platform-specific top-level declarations get a suffix (`Platform.jvm.kt`, `Platform.ios.kt`); common stays unsuffixed (`Platform.kt`).

Multiple related declarations in one file are encouraged, as long as the file stays within a few hundred lines.

**Class layout — fixed order:**

1. Property declarations and initializer blocks
2. Secondary constructors
3. Method declarations
4. Companion object

Group related methods together. **Do not** sort alphabetically or by visibility, and do not separate regular methods from extension methods. Pick one direction (high-level first, or vice versa) and stick to it.

Nested classes go next to the code that uses them; nested classes used only externally go after the companion object.

**Interface implementations:** keep member order matching the interface.
**Overloads:** always adjacent.

## 3. Formatting

**Indent: 4 spaces. No tabs. Java-style braces.**

Opening brace at end of line, closing brace aligned with the construct's start.

**Whitespace:**

- Around binary operators (`a + b`). Exception: no spaces around `..` (`0..i`).
- After `if` / `when` / `for` / `while` keyword, before `(`.
- NOT before `(` of constructor / method declaration or call.
- NOT after `(` or `[`; NOT before `]` or `)`.
- NOT around `.`, `?.`, `::`, type angle brackets, nullable `?`.
- NOT around unary operators (`a++`).
- After `//` always: `// comment`.
- Around `:` when separating type from supertype, delegating to a constructor, or after `object`. Otherwise no space before `:`. Always space after `:`.

**Avoid horizontal alignment.** Renaming a variable shouldn't reflow neighboring lines.

**Modifier order** (when multiple, in this exact order):

```
public / protected / private / internal
expect / actual
final / open / abstract / sealed / const
external
override
lateinit
tailrec
vararg
suspend
inner
enum / annotation / fun
companion
inline / value
infix
operator
data
```

Annotations precede all modifiers, on their own line (annotations without args may share a line; a single bare annotation may share with the declaration). Omit redundant modifiers (`public`) — unless writing a library.

**File annotations** sit after the file comment, before `package`, separated from `package` by a blank line.

**Class headers:**

- Short → one line: `class Person(id: Int, name: String)`.
- Long → each param on its own line, `)` on its own line; supertype call on the same line as `)`. Multiple supertypes: superclass first, each interface on its own line.

**Functions:**

- Multiline signature → params indented 4 spaces, `)` on its own line, trailing comma encouraged.
- Single-expression body → use `=`, drop braces and explicit `return`.
- Long expression body → `=` on signature line; expression on next line, indented 4.

**Properties:**

- Simple read-only one-liner: `val isEmpty: Boolean get() = size == 0`.
- Complex: `get` / `set` on separate lines.
- Long initializer: line break after `=`, indent 4.

**Control flow:**

- Multiline `if` / `when` condition → braces around body; indent continuation 4; `)` and `{` on the same line.
- `else` / `catch` / `finally` / `while` (of `do-while`): on the same line as the preceding `}`.
- `when`: short branches without braces; multi-line branches separated by a blank line.

**Lambdas:**

- Spaces around `{`, `}`, and `->`.
- Trailing lambda outside parens when possible (`list.filter { it > 10 }`).
- Use `it` for short, non-nested lambdas; name parameters explicitly when nested.
- No space between label and `{`: `lit@{`.

**Chained calls:** `.` or `?.` on the next line, single indent.

**Trailing commas:** encouraged at declaration site (params, args, enums, `when` entries, destructuring, type params, lambda params, indexing suffix); optional at call site.

## 4. Idiomatic Kotlin

**`val` over `var`** if not reassigned. Always.

**Immutable collection types** (`List`, `Set`, `Map`) for non-mutated values. Use `listOf` not `arrayListOf` unless mutation is intended.

**Default parameter values** over overloads.

**Expression form** of `try` / `if` / `when` when producing a value.

**`if` for binary** conditions; **`when` for 3+** branches.

**Nullable `Boolean` in a condition:** check `if (value == true)` or `if (value == false)`.

**Guard conditions in `when`:** wrap combined boolean expressions in parentheses.

**Open-ended ranges:** `..<` not `..n-1`.

**String templates** over concatenation. Use `${...}` only for non-trivial expressions; just `$name` for simple identifiers.

**Multiline strings** over embedded `\n`. Use `trimIndent` (no internal indentation needed) or `trimMargin` (internal indentation needed).

**Higher-order functions** (`filter`, `map`) over loops — but prefer regular `for` over `forEach` (unless receiver nullable or part of a chain). Weigh performance for complex chains.

**Extension functions** liberally. Restrict visibility (private, internal, local, member) as appropriate.

**Property vs function** — prefer a property when the algorithm doesn't throw, is cheap (or cached), and returns the same result for unchanged object state. Otherwise function.

**Type aliases** for repeated functional types or generic types.

**Factory functions:** give a distinct name from the class unless semantics are truly identical to a constructor; prefer factories over overloaded constructors that can't be merged with default parameters.

**Infix functions:** only for symmetric two-object operations (`and`, `to`, `zip`). Never if it mutates the receiver.

**Named arguments** when calling functions with multiple same-typed primitives or any `Boolean`, unless context makes meaning obvious.

**Platform types:** explicit Kotlin type required for public functions/methods returning a platform type, and for any package- or class-level property initialized from a platform expression. Locals may infer.

**Avoid multiple labeled returns** in a lambda. Restructure to a single exit point, or convert to an anonymous function. Do not label-return the last statement.

**Scope functions** (`let` / `run` / `with` / `apply` / `also`): see kotlinlang.org/docs/scope-functions.html.

## 5. Avoid Redundant Constructs

If the IDE marks it redundant, remove it. Do not keep syntax "for clarity".

- Omit `: Unit` return type.
- Omit semicolons.
- Omit braces in `${name}` for simple variables — `"$name has ${children.size} children"`.
- Omit `public` and other redundant modifiers (outside library code).

## 6. Documentation Comments

KDoc:

- Long → `/**` on its own line, each subsequent line begins with `*`.
- Short → single-line `/** ... */`.
- Avoid `@param` / `@return` — fold descriptions into prose and link parameters as `[paramName]`. Use the tags only when length forces it.

## 7. Library Code — Extra Rules

When writing a library (not application code):

- Always specify member visibility explicitly.
- Always specify function return types and property types explicitly.
- KDoc on every public member (trivial overrides excepted).

---

## Code Review Checklist

Walk through in this order when reviewing Kotlin code:

1. **Naming** — packages lowercase, classes UpperCamel, functions lowerCamel, constants SCREAMING_SNAKE? Acronym rule (2-letter vs 3+) correct? `sort`/`sorted` mutation-vs-copy pattern respected? No `Manager` / `Util`?
2. **File** — name matches contents? Multiplatform suffix correct?
3. **Class layout** — properties → secondary constructors → methods → companion? Related methods grouped, not alphabetized?
4. **Formatting** — 4-space indent, brace placement, whitespace rules, modifier order?
5. **Idiomatic** — `val` where possible? Immutable collection types? Expression bodies for single-expression functions? `if` for binary, `when` for 3+? `..<` for exclusive ends? String templates? Higher-order functions over loops (forEach exception)?
6. **Redundancy** — `: Unit` removed? Semicolons removed? Redundant `${}` simplified? Redundant `public` removed?
7. **KDoc** — public API documented? `@param` / `@return` avoided in favor of inline `[paramName]` links?
8. **Library code** (if applicable) — explicit visibility, explicit return / property types, KDoc on every public member?
