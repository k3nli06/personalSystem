import { useState } from 'react';
import Header from './Header';
import TimeRecords from './TimeRecords';
import VacationRequests from './VacationRequests';
import { Clock, Calendar } from 'lucide-react';

export default function EmployeeDashboard({ user, onLogout }) {
  const [activeTab, setActiveTab] = useState('time');

  return (
    <div className="min-h-screen bg-gray-50">
      <Header user={user} onLogout={onLogout} />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-2">
            Bienvenido, {user.full_name}
          </h2>
          <p className="text-gray-600">Gestiona tus horas y solicitudes de vacaciones</p>
        </div>

        <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
          <div className="border-b border-gray-200">
            <nav className="flex">
              <button
                onClick={() => setActiveTab('time')}
                className={`flex-1 flex items-center justify-center gap-2 px-6 py-4 font-medium transition-colors ${
                  activeTab === 'time'
                    ? 'bg-blue-50 text-blue-600 border-b-2 border-blue-600'
                    : 'text-gray-600 hover:bg-gray-50'
                }`}
              >
                <Clock className="w-5 h-5" />
                Registro de Horas
              </button>
              <button
                onClick={() => setActiveTab('vacation')}
                className={`flex-1 flex items-center justify-center gap-2 px-6 py-4 font-medium transition-colors ${
                  activeTab === 'vacation'
                    ? 'bg-blue-50 text-blue-600 border-b-2 border-blue-600'
                    : 'text-gray-600 hover:bg-gray-50'
                }`}
              >
                <Calendar className="w-5 h-5" />
                Solicitudes de Vacaciones
              </button>
            </nav>
          </div>

          <div className="p-6">
            {activeTab === 'time' && <TimeRecords userId={user.id} />}
            {activeTab === 'vacation' && <VacationRequests userId={user.id} userRole={user.role} />}
          </div>
        </div>
      </div>
    </div>
  );
}
