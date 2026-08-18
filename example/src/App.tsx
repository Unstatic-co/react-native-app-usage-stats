import { useState } from 'react';
import { Button, ScrollView, StyleSheet, Text, View } from 'react-native';

import {
  requestUsageStatsPermission,
  hasUsageStatsPermission,
  queryAppUsageSessions,
  queryAppUsageSessionsByPackageName,
  queryAggregatedUsageStats,
  queryAggregatedUsageStatsByPackageName,
} from 'rn-app-usage-stats';

export default function App() {
  const [permission, setPermission] = useState<boolean | null>(null);
  const [result, setResult] = useState<unknown>(null);

  const checkPermission = async () => {
    try {
      const granted = await hasUsageStatsPermission();
      setPermission(granted);
    } catch (error) {
      console.error(error);
    }
  };

  const requestPermission = async () => {
    try {
      const granted = await requestUsageStatsPermission();
      setPermission(granted);
    } catch (error) {
      console.error(error);
    }
  };

  const loadSessions = async () => {
    try {
      const endRange = Date.now();
      const startRange = endRange - 24 * 60 * 60 * 1000;

      const stats = await queryAppUsageSessions(startRange, endRange);

      setResult(stats);
    } catch (error) {
      console.error(error);
    }
  };

  const loadSessionsByPackage = async () => {
    try {
      const endRange = Date.now();
      const startRange = endRange - 24 * 60 * 60 * 1000;

      const stats = await queryAppUsageSessionsByPackageName(
        'com.android.settings',
        startRange,
        endRange
      );

      setResult(stats);
    } catch (error) {
      console.error(error);
    }
  };

  const loadAggregatedStats = async () => {
    try {
      const endRange = Date.now();
      const startRange = endRange - 24 * 60 * 60 * 1000;

      const stats = await queryAggregatedUsageStats(startRange, endRange);

      setResult(stats);
    } catch (error) {
      console.error(error);
    }
  };

  const loadAggregatedStatsByPackage = async () => {
    try {
      const endRange = Date.now();
      const startRange = endRange - 24 * 60 * 60 * 1000;

      const stats = await queryAggregatedUsageStatsByPackageName(
        'com.android.settings',
        startRange,
        endRange
      );

      setResult(stats);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>App Usage Stats</Text>

      <Text style={styles.permission}>
        Permission:{' '}
        {permission === null
          ? 'Unknown'
          : permission
            ? 'Granted'
            : 'Not granted'}
      </Text>

      <View style={styles.button}>
        <Button title="Check Permission" onPress={checkPermission} />
      </View>

      <View style={styles.button}>
        <Button
          title="Request Usage Stats Permission"
          onPress={requestPermission}
        />
      </View>

      <View style={styles.button}>
        <Button title="Query Usage Sessions" onPress={loadSessions} />
      </View>

      <View style={styles.button}>
        <Button
          title="Query Usage Sessions By Package"
          onPress={loadSessionsByPackage}
        />
      </View>

      <View style={styles.button}>
        <Button title="Query Aggregated Stats" onPress={loadAggregatedStats} />
      </View>

      <View style={styles.button}>
        <Button
          title="Query Aggregated Stats By Package"
          onPress={loadAggregatedStatsByPackage}
        />
      </View>

      {result !== null && (
        <View style={styles.resultContainer}>
          <Text style={styles.resultTitle}>Result</Text>

          <Text style={styles.result}>{JSON.stringify(result, null, 2)}</Text>
        </View>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 24,
    paddingTop: 80,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
  },
  permission: {
    fontSize: 16,
    marginBottom: 24,
  },
  button: {
    marginBottom: 12,
  },
  resultContainer: {
    marginTop: 24,
  },
  resultTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  result: {
    fontFamily: 'monospace',
    fontSize: 12,
  },
});
