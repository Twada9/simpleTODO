# simpleTODO

シンプルなTODO管理Androidアプリケーション

## 概要

simpleTODOは、Jetpack ComposeとRoom Databaseを使用して構築された、シンプルで使いやすいTODO管理アプリです。タスクの作成、編集、削除、優先度の設定など、基本的なTODO管理機能を提供します。

## 主な機能

- ✅ TODOの追加・編集・削除
- 🎯 優先度設定（高・中・低）
- 📅 日付管理
- 💾 Room Databaseによるローカルデータ保存
- 🎨 Material Design 3を採用したモダンなUI

## 技術スタック

- **言語**: Kotlin
- **UIフレームワーク**: Jetpack Compose
- **データベース**: Room Database
- **DIフレームワーク**: Koin
- **アーキテクチャ**: MVVM (Model-View-ViewModel)
- **非同期処理**: Kotlin Coroutines + Flow
- **最小SDKバージョン**: API 24 (Android 7.0)
- **ターゲットSDKバージョン**: API 35

## プロジェクト構成

```
app/src/main/java/com/example/simpletodo/
├── DataAccess/
│   └── TodoDao.kt              # Room DAO
├── Database/
│   ├── DatabaseCallbacks.kt    # データベースコールバック
│   ├── DatabaseMigrations.kt   # マイグレーション定義
│   └── TodoDatabase.kt         # Room データベース
├── Model/
│   └── Todo.kt                 # Todoデータモデル
├── ui/
│   ├── components/
│   │   ├── AddButton.kt        # 追加ボタンコンポーネント
│   │   ├── TodoBottomSheet.kt  # 編集用ボトムシート
│   │   └── TodoCard.kt         # TODOカードコンポーネント
│   ├── screen/
│   │   └── TodoListScreen.kt   # メイン画面
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── ViewModel/
│   └── MainViewModel.kt        # メインViewModel
├── MainActivity.kt             # メインアクティビティ
└── MainApplication.kt          # アプリケーションクラス
```

## セットアップ

### 必要な環境

- Android Studio Ladybug | 2024.2.1 以上
- JDK 11 以上
- Android SDK API 35

### ビルド手順

1. リポジトリをクローン
```bash
git clone <repository-url>
cd simpleTODO
```

2. Android Studioでプロジェクトを開く

3. Gradle Syncを実行

4. エミュレーターまたは実機でアプリを実行

## テスト

プロジェクトにはユニットテストが含まれています。

```bash
./gradlew test
```

## 依存関係

主要な依存関係：

- **Room**: 2.6.1
- **Koin**: 4.1.0
- **Compose BOM**: (最新)
- **MockK**: 1.14.7 (テスト用)
- **Coroutines Test**: 1.7.3 (テスト用)

詳細は [app/build.gradle.kts](app/build.gradle.kts) を参照してください。

## ライセンス

このプロジェクトは個人プロジェクトです。

## 開発メモ

- DIコンテナとしてKoinを採用
- Previewはモックを使用して実装
- データベースマイグレーションをサポート
- エラーハンドリングを実装
