# zebra-label-generator

A Java library to generate Zebra printer strings for labels including barcodes and printer settings

## Project Status

This is an under construction project designed to allow developers to chiefly and programmatically create ZPL label
format commands.

## Current Features

- Constants for Most Zebra ZPL II commands

## Planned Features

- DPI Agnostic Label Creation (This sparked the idea for the initial project)

## Reference Documentation

- [Zebra Programming Guide 2018](https://support.zebra.com/cpws/docs/zpl/zpl-zbi2-pm-en.pdf)

## Inspirations

- [W3 Blog France / Zebra ZPL Project](https://github.com/w3blogfr/zebra-zpl)
  I was originally going to make use of this project but for many reasons i chose not to use it or fork it. In
  particular i found i was having to rewrite a lot of the logic for my own use cases, and it wouldn't solve my initial
  wishes for DPI agnostic programmatic label creation.

## Testing Labels

You can test the ZPL output generated by this library using
the [Labelary Online ZPL Viewer](https://labelary.com/viewer.html).
Simply copy the generated ZPL code and paste it into the viewer to preview how your labels will look when printed.

The Labelary Viewer allows you to:

- Visualize labels before printing
- Verify proper placement of elements
- Check barcode formatting
- Test different label sizes
- Validate ZPL syntax

This is particularly useful during development to ensure your labels are formatted correctly without
needing physical access to a Zebra printer.
