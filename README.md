# WinCounts

## 概要

ワールド移動時に指定したアイテムを削除した上でポイントに変換し、後からポイントが最も高いプレイヤーを運営が確認できるようにするプラグイン

## 使い方

1. [Release](https://github.com/AzisabaNetwork/WinCounts/releases/latest)からJarをダウンロードしてpluginsに導入する
2. サーバーを起動して config.yml を生成する
3. MySQLの情報を記入してサーバーを再起動する
4. `/wincounts setitem <イベント名>` を実行して、ポイントに変換したいアイテムを登録する (複数登録可)
5. イベント終了後、`/wincounts result <イベント名>` で最もポイントが高かったプレイヤーを確認する

## コマンド

|コマンド|説明|
|:---|:---|
|/wincounts list|登録されているイベント名一覧を見る|
|/wincounts getitem <イベント名>|登録されているアイテムを取得する|
|/wincounts setitem <イベント名>|手に持っているアイテムを登録する|
|/wincounts clearitems <イベント名>|登録されたアイテムを全消しする(変換されたポイントは消えない)|
|/wincounts result <イベント名>|最もポイントが高いプレイヤーを確認する(同列のプレイヤーが居る場合全員表示)|
