package utils;

public interface GenerateDataListener {

    void uniformData(int dimensions, int points, int spread);
    void clusteredData(int dimensions, int points, int spread, int clusters, int clustersSpread);
    void fileData(String file);

    void process(int method);

}
