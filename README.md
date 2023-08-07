# Kigen Engine

Kigen is a game engine based on [lwjgl](https://www.lwjgl.org/) written in Clojure.


## Installation

TODO Upload this to Clojars. I will upload this when all base features are completed.

## Usage

Use ```lein with-profile <platform-name> <command-to-run>```. For example, to run the project in macos I use:

```Bash
lein with-profile macos-arm64 run
```


## Options

At the moment the project can be run using the following platform names:
- macos-arm64
- windows-x86
- windows-amd64
- linux-amd64
- linux-arm64
- linux-arm32

These options are only tested in macos-arm64 and windows-amd64. If you want to contribute testing the test game
in other platforms, please share the results(success, error) with an issue.

### Bugs

TODO

## License

Copyright (c) 2012-2023 Enyert Vinas

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
