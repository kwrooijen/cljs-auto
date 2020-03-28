[![Clojars Project](https://img.shields.io/clojars/v/cljs-auto.svg)](https://clojars.org/cljs-auto)

# cljs-auto

A tool to automatically convert EDN files to Clojurescript, and automatically
require namespaces

## Rationale

The main reason for this library was to solve two problems with the combination
of Clojurescript and Integrant. However this can be used in a more general way

### 1. Clojurescript can't read EDN files

In Clojurescript (unless using node.js) you cannot read files. This can only be
done through HTTP requests. In Clojurescript you'd write EDN directly in your
source files, mixing source and configuration.

This tool let's you aggregate EDN files and place them in a Clojurescript file.
This way you can still write EDN, and it still gets minified.


### 2. Clojurescript can't dynamically require namespaces

Integrant has a feature to automatically require namespaces based on the keys in
your configuration. This feature is not supported in Clojurescript because of
limitations.

This tool will add require statements to the generated file based on the
directory you choose to watch. This way you only have to require the generated
file to include all multimethods.

## Usage

Add the following alias to your `deps.edn` file, and configure as needed.

``` clojure
{:aliases
 {:cljs-auto
  {:main-opts
   ["-m cljs-auto.core"
    "-p resources/config"            ;; EDN files to insert in generated file
    "-P src/my-app/files-to-require" ;; Clojurescript files to require
    "-o src/my-app/config.cljs"      ;; Output file name
    "-ns my-app.config"              ;; Namespace of the generated file
    "--integrant"]                   ;; Include Integrant namespace and EDN readers
   :extra-deps
   {cljs-auto
    {:mvn/version "0.0.1"}}}}}
```

Then run the alias in the terminal.

``` sh
clj -A:cljs-auto
```

If you want to automatically watch for changes, you can add the `--watch flag`

``` sh
clj -A:cljs-auto --watch
```

This will generate a new file based on your options, with a `def` called
`config`. Your EDN files will be merged and placed here.

## Options

| Option | Option      | Description                                             |
|:-------|:------------|:--------------------------------------------------------|
| -o     | --output    | An output file must be specified using the `-o` option. |
| -p     | --edn-path  | Root path of EDN files to merge                         |
| -P     | --cljs-path | Root path of Clojurescript files to require             |
| -ns    | --namespace | Namespace of the generated cljs file                    |
| -w     | --watch     | Watch PATH to see if any files change and process them. |
|        | --integrant | Use Integrant readers (ig/ref ig/refset)                |
| -h     | --help      | Show help information                                   |

## License

Copyright © 2020 Kevin William van Rooijen

This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary Licenses when the conditions for such availability set forth in the Eclipse Public License, v. 2.0 are satisfied: GNU General Public License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any later version, with the GNU Classpath Exception which is available at https://www.gnu.org/software/classpath/license.html.
