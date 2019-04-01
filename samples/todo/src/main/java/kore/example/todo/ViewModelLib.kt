package kore.example.todo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

fun <T : ViewModel> FragmentActivity.getKore(modelClass: Class<T>): T =
    ViewModelProviders.of(this).get(modelClass)

fun <T : ViewModel> Fragment.getActivityKore(activity: FragmentActivity, modelClass: Class<T>): T =
    ViewModelProviders.of(activity).get(modelClass)

fun <T : ViewModel> Fragment.getKore(modelClass: Class<T>): T =
    ViewModelProviders.of(this).get(modelClass)
