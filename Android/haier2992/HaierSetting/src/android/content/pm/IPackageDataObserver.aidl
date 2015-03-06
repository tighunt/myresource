package android.content.pm;

oneway interface IPackageDataObserver {
    void onRemoveCompleted(in String packageName, boolean succeeded);
}