# Alchitry Parser V2

## Tools Used

* Linux or Windows (macOS can be used for the parser but the build tools rely upon Vivado/iceCube2 and do not support
  macOS)
* [Intellij](https://www.jetbrains.com/idea/)
* Gradle
* Kotlin (manged in gradle script)
* [Antlr 4](https://www.antlr.org/) ([Intellij Plugin](https://plugins.jetbrains.com/plugin/7358-antlr-v4))
* Git
* [Compose](https://www.jetbrains.com/lp/compose-desktop/) (for UI)

## Setup

Clone the repo and open it as a project in Intellij. The gradle build files should cause Intellij to install the correct
versions of all libraries and Kotlin.

You may want to install the Antlr plugin for syntax highlighting of the Lucid.g4 grammar. (File->Settings->Plugins)

Once the gradle sync is done, you should be able to run the tests by opening the test file and clicking the green arrow
in the code editor gutter.

## Lucid Background

* https://alchitry.com/lucid-reference
* https://alchitry.com/lucid

## Parser Overview

The legacy code can be
found [here](https://github.com/alchitry/Alchitry-Labs/tree/master/src/com/alchitry/labs/parsers/tools/lucid).

This code has many `LucidBaseListeners` that are all interdependent making it a bit of a mess. The new version is
drastically different but some things may still be helpful as a reference.

### Value Representation

Lucid values of are made up of bits. Each bit can take one of five values. These are represented in the `BitValue`
class.

`B0` and `B1` are the obvious 0 and 1 values.

`Bx` is used when the value could be either 0 or 1 but we either don't care (when assigning it directly) or don't know
(bad computation).

`Bz` is used to represent high-impedance and should only ever be used by a top-level output.

`Bu` is a special internally used value and is used to track when a signal hasn't been assigned a value.

`BitValue` are packed into `BitList` to make them hold more than one bit. `BitList` is basically a binary number that
has some helper functions for manipulating the values such as `or`, `and`, and `xor`.

The `BitList` interface also has a `signed` property to specify if the contained value is a signed representation (aka
2's compliment).

The class `MutableBitList` is a mutable implementation of the `BitList` interface and can be initialized in a few
convent ways.

Lucid supports four types of values which all extend the `Value` class.

The most basic one is `SimpleValue` this is a value that basically just wraps a `BitList` and represents a single 1D
array of bits.

`ArrayValue` is used to represent arrays. Each element in the array *must* be the same size. The elements themselves
are `Value`s and could be another `ArrayValue` for even deeper arrays or `StructValue`.

`StructValue` is used to represent a key-value map of signals similar to a struct in C. The declaration looks like this.

```  
struct in {
    addr[28],
    cmd[3],
    en,
    wr_data[128],
    wr_en,
    wr_mask[16]
  }
  ```

This would define a struct with name `in`. The various components of it can be accessed with the
syntax `SIGNAL_NAME.STRUCT_MEMBER` for example `SIGNAL_NAME.wr_en` to access the `wr_en` component.

The final `Value` type is `UndefinedValue`. The current only use for this is for module parameters. Module parameters
are used to make modules more generic by allowing a constant to be provided at the time it is instantiated (used). The
parameter's value would be set to `UndefinedValue` when the module is being evaluated but hasn't been instantiated yet.
This value will be replaced with a real value once the module is instantiated and error checking on any `UndefinedValue`
can properly happen then.

When possible, a known width can be set inside the `UndefinedValue` by passing in the `width` parameter. This is helpful
for early error checking.

#### SignalWidth

The class `SignalWidth` is used to represent the width of a signal without an associated value. This is mainly used for
error checking purposes.

There are again four types that correspond to the four value types. `SimpleWidth`, `ArrayWidth`, `StructWidth`,
and `UndefinedSimpleWidth`.

`UndefinedSimpleWidth` is used to specify when we know a value is a 1D array of bits, but we don't know how many. This
is
the default width for any module parameters.

The `Value` class has as property `signalWidth` that will return the `SignalWidth` for the value. This value is
typically computed from the `Value` directly except for `UndefinedValued` which simply uses its `width` property.

## ExprParser

The `ExprParser` class is responsible for parsing any expressions. This is essentially any child of the `expr` rule in
the Lucid grammar.

The parser has four main outputs.

The `values` property is a map of every computed `Value` at each node in
the `ParseTree`. For example, `2 + 2` would have `ExprNum` nodes with values of 2 and a `ExprAddSub` node with value 4.

The `constant` property is a boolean map specifying if the value at this node is a known constant. In the
previous `2 + 2` example, every node would be true. It will be false if a value is from a signal that could change over
time (like an input). This is again for error checking. Some functions, like `$clog2()`, can only be used with constant
values as they must be computed at synthesis time.

The `bounds` property is a map of `IntRange` used to keep track of the selected bits of an array.

Finally, `ExprParser` also is responsible for providing warnings and errors as it parses to the
provided `ErrorListener`.