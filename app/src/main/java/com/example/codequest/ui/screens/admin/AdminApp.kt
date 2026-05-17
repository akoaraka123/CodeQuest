package com.example.codequest.ui.screens.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codequest.data.LocalPasswordResetRepository
import com.example.codequest.data.LocalUserRepository
import com.example.codequest.data.LocalContentRepository
import com.example.codequest.model.UserRole
import com.example.codequest.model.UserStatus
import com.example.codequest.ui.components.GlassCard
import com.example.codequest.ui.theme.BackgroundEnd
import com.example.codequest.ui.theme.BackgroundStart
import com.example.codequest.ui.theme.PrimaryCyan
import com.example.codequest.ui.theme.TextMuted

enum class AdminTab {
    DASHBOARD,
    USERS,
    COURSES,
    REPORTS
}

private sealed class AdminRoute {
    data class Tab(val tab: AdminTab) : AdminRoute()
    data class UserDetail(val userId: String) : AdminRoute()
    data class AddEditUser(val userId: String?) : AdminRoute()
    data object Notifications : AdminRoute()
}

@Composable
fun AdminApp(
    onLogout: () -> Unit
) {
    var route by remember { mutableStateOf<AdminRoute>(AdminRoute.Tab(AdminTab.DASHBOARD)) }

    BackHandler(enabled = route !is AdminRoute.Tab) {
        route = when (val r = route) {
            is AdminRoute.UserDetail -> AdminRoute.Tab(AdminTab.USERS)
            is AdminRoute.AddEditUser ->
                if (r.userId != null) AdminRoute.UserDetail(r.userId) else AdminRoute.Tab(AdminTab.USERS)
            AdminRoute.Notifications -> AdminRoute.Tab(AdminTab.DASHBOARD)
            is AdminRoute.Tab -> r
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BackgroundStart, BackgroundEnd)))
    ) {
        when (val r = route) {
            is AdminRoute.Tab -> {
                AdminTabContent(
                    tab = r.tab,
                    onOpenUser = { route = AdminRoute.UserDetail(it) },
                    onAddUser = { route = AdminRoute.AddEditUser(null) },
                    onOpenNotifications = { route = AdminRoute.Notifications },
                    onLogout = onLogout
                )
                AdminBottomBar(
                    selected = r.tab,
                    onSelect = { route = AdminRoute.Tab(it) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
            is AdminRoute.UserDetail -> {
                AdminUserDetailsScreen(
                    userId = r.userId,
                    onBack = { route = AdminRoute.Tab(AdminTab.USERS) },
                    onEdit = { route = AdminRoute.AddEditUser(r.userId) },
                    onToggleStatus = { LocalUserRepository.toggleUserStatus(r.userId) },
                    onDelete = {
                        LocalUserRepository.deleteUser(r.userId)
                        route = AdminRoute.Tab(AdminTab.USERS)
                    }
                )
            }
            is AdminRoute.AddEditUser -> {
                AddEditUserScreen(
                    userId = r.userId,
                    onCancel = {
                        route = if (r.userId != null) {
                            AdminRoute.UserDetail(r.userId)
                        } else {
                            AdminRoute.Tab(AdminTab.USERS)
                        }
                    },
                    onSaved = { id ->
                        route = AdminRoute.UserDetail(id)
                    }
                )
            }
            AdminRoute.Notifications -> {
                AdminNotificationsScreen(
                    onBack = { route = AdminRoute.Tab(AdminTab.DASHBOARD) }
                )
            }
        }
    }
}

@Composable
private fun AdminTabContent(
    tab: AdminTab,
    onOpenUser: (String) -> Unit,
    onAddUser: () -> Unit,
    onOpenNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val students = LocalUserRepository.users.filter { it.role == UserRole.STUDENT }
    val allUsers = LocalUserRepository.users.toList()
    val activeStudents = students.count { it.status == UserStatus.ACTIVE }
    val courses = LocalContentRepository.visibleCourses
    val totalLessons = courses.sumOf { it.lessons.size }
    val totalXp = students.sumOf { it.totalXP }
    val avgXp = if (students.isNotEmpty()) totalXp / students.size else 0

    Column(modifier = Modifier.fillMaxSize()) {
        when (tab) {
            AdminTab.DASHBOARD -> AdminDashboardScreen(
                totalStudents = students.size,
                activeStudents = activeStudents,
                totalCourses = courses.size,
                totalLessons = totalLessons,
                averageXp = avgXp,
                hasUnreadNotifications = LocalPasswordResetRepository.hasUnreadRequests(),
                onNotificationsClick = onOpenNotifications,
                onLogout = onLogout
            )
            AdminTab.USERS -> AdminUserManagementScreen(
                users = allUsers,
                onViewUser = onOpenUser,
                onAddUser = onAddUser,
                onToggleStatus = { LocalUserRepository.toggleUserStatus(it) }
            )
            AdminTab.COURSES -> AdminCourseOverviewScreen()
            AdminTab.REPORTS -> AdminReportsScreen()
        }
    }
}

@Composable
private fun AdminBottomBar(
    selected: AdminTab,
    onSelect: (AdminTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        AdminTab.DASHBOARD to "Dashboard",
        AdminTab.USERS to "Users",
        AdminTab.COURSES to "Courses",
        AdminTab.REPORTS to "Reports"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 24.dp) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items.forEach { (tab, label) ->
                    val on = selected == tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onSelect(tab) }
                            .padding(vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (on) PrimaryCyan else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (on) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
