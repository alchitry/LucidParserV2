package com.alchitry.labs.parsers.lucidv2.parsers

enum class ParseStage {
    ModuleInternals, // module internal (always blocks)
    Drivers, // check for driven signals
    Evaluation, // runtime
    ErrorCheck // IDE error check
}