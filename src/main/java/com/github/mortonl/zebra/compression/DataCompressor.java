package com.github.mortonl.zebra.compression;

/**
 * Abstract base class for data compression implementations used in Zebra label generation.
 * Provides a common interface for different compression algorithms that can be applied
 * to graphic data before transmission to the printer.
 */
public abstract class DataCompressor
{

    /**
     * Compresses the provided data using the specific compression algorithm.
     * 
     * @param data the raw data to compress
     * @param bytesPerRow the number of bytes per row in the data
     * @return the compressed data string
     */
    public abstract String compressData(String data, int bytesPerRow);
}
