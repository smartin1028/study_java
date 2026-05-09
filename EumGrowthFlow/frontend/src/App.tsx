/**
 * EumGrowthFlow 메인 애플리케이션 컴포넌트
 *
 * ReactFlow 기반의 AI 에이전트 워크플로우 빌더.
 * 노드 설정, 워크플로우 실행, 암호화 저장/불러오기, LLM 채팅 기능을 제공한다.
 * 모든 LLM API 호출은 백엔드 서버를 통해 처리된다.
 */

import { useCallback, useState } from 'react';
import ReactFlow, {
  MiniMap,
  Controls,
  Background,
  useNodesState,
  useEdgesState,
  addEdge,
  BackgroundVariant,
} from 'reactflow';
import 'reactflow/dist/style.css';

import AIAgentNode from './components/nodes/AIAgentNode';
import Sidebar from './components/Sidebar';
import NodeConfigPanel from './components/NodeConfigPanel';
import ExecutionPanel from './components/ExecutionPanel';
import ChatInterface from './components/ChatInterface';
import PasswordModal from './components/PasswordModal';
import BackendTokenModal from './components/BackendTokenModal';
import { workflowExecutor } from './services/workflowExecutor';
import {
  downloadEncryptedWorkflow,
  loadEncryptedWorkflow,
} from './utils/encryption';
import {
  setBearerToken,
  hasBearerToken,
  clearBearerToken,
} from './services/llmClient';
import './App.css';

// ReactFlow 커스텀 노드 타입 등록
const nodeTypes = {
  aiAgent: AIAgentNode,
};

// 초기 캔버스 상태: 시작 노드 하나
const initialNodes = [
  {
    id: '1',
    type: 'aiAgent',
    position: { x: 250, y: 100 },
    data: {
      label: 'Start Node',
      agentType: 'input',
      description: '워크플로우 시작점',
    },
  },
];

const initialEdges: any[] = [];

function App() {
  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
  const [selectedNode, setSelectedNode] = useState<any>(null);
  const [executionResults, setExecutionResults] = useState<any[]>([]);
  const [isRunning, setIsRunning] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const [showChat, setShowChat] = useState(false);
  const [chatConfig, setChatConfig] = useState<any>(null);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordMode, setPasswordMode] = useState<'save' | 'load'>('save');
  const [pendingFile, setPendingFile] = useState<File | null>(null);
  const [showTokenModal, setShowTokenModal] = useState(false);

  const onConnect = useCallback(
    (params: any) => setEdges((eds) => addEdge(params, eds)),
    [setEdges],
  );

  const onNodeClick = useCallback((_event: any, node: any) => {
    setSelectedNode(node);
  }, []);

  const onPaneClick = useCallback(() => {
    setSelectedNode(null);
  }, []);

  const handleSaveNodeConfig = useCallback(
    (config: any) => {
      setNodes((nds) =>
        nds.map((node) => {
          if (node.id === config.id) {
            return {
              ...node,
              data: {
                ...node.data,
                label: config.label,
                prompt: config.prompt,
                model: config.model,
              },
            };
          }
          return node;
        }),
      );
    },
    [setNodes],
  );

  const onDragOver = useCallback((event: React.DragEvent) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback(
    (event: React.DragEvent) => {
      event.preventDefault();

      const type = event.dataTransfer.getData('application/reactflow');
      const label = event.dataTransfer.getData('label');
      const agentType = event.dataTransfer.getData('agentType');
      const description = event.dataTransfer.getData('description');

      const position = {
        x: event.clientX - 250,
        y: event.clientY - 100,
      };

      const newNode = {
        id: `${Date.now()}`,
        type,
        position,
        data: { label, agentType, description },
      };

      setNodes((nds) => nds.concat(newNode));
    },
    [setNodes],
  );

  const onSave = useCallback(() => {
    const flow = { nodes, edges };
    localStorage.setItem('workflow', JSON.stringify(flow));
    alert('워크플로우가 저장되었습니다!');
  }, [nodes, edges]);

  const onLoad = useCallback(() => {
    const flowData = localStorage.getItem('workflow');
    if (flowData) {
      const flow = JSON.parse(flowData);
      setNodes(flow.nodes || []);
      setEdges(flow.edges || []);
      alert('워크플로우가 로드되었습니다!');
    } else {
      alert('저장된 워크플로우가 없습니다.');
    }
  }, [setNodes, setEdges]);

  const onClear = useCallback(() => {
    setNodes([]);
    setEdges([]);
  }, [setNodes, setEdges]);

  const onExecute = useCallback(async () => {
    setIsRunning(true);
    setShowResults(true);
    setExecutionResults([]);

    try {
      const results = await workflowExecutor.executeWorkflow(nodes, edges);
      setExecutionResults(results);
    } catch (error: any) {
      setExecutionResults([
        {
          nodeId: 'error',
          success: false,
          error: error.message,
        },
      ]);
    } finally {
      setIsRunning(false);
    }
  }, [nodes, edges]);

  const handleOpenChat = useCallback(() => {
    if (selectedNode) {
      setChatConfig({
        model: selectedNode.data.model,
      });
      setShowChat(true);
      setSelectedNode(null);
    }
  }, [selectedNode]);

  // 암호화 저장
  const handleEncryptedSave = useCallback(() => {
    setPasswordMode('save');
    setShowPasswordModal(true);
  }, []);

  const handlePasswordConfirm = useCallback(
    (password: string) => {
      if (passwordMode === 'save') {
        try {
          downloadEncryptedWorkflow(nodes, edges, password);
          alert('🔒 암호화된 워크플로우가 다운로드되었습니다!');
        } catch (error: any) {
          alert(`❌ 저장 실패: ${error.message}`);
        }
      } else if (passwordMode === 'load' && pendingFile) {
        loadEncryptedWorkflow(pendingFile, password)
          .then(({ nodes: loadedNodes, edges: loadedEdges }) => {
            setNodes(loadedNodes);
            setEdges(loadedEdges);
            alert('✅ 암호화된 워크플로우가 로드되었습니다!');
          })
          .catch((error: any) => {
            alert(`❌ 불러오기 실패: ${error.message}`);
          })
          .finally(() => {
            setPendingFile(null);
          });
      }
      setShowPasswordModal(false);
    },
    [passwordMode, nodes, edges, pendingFile, setNodes, setEdges],
  );

  // 암호화 파일 불러오기
  const handleEncryptedLoad = useCallback(() => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.eum';
    input.onchange = (e: any) => {
      const file = e.target.files[0];
      if (file) {
        setPendingFile(file);
        setPasswordMode('load');
        setShowPasswordModal(true);
      }
    };
    input.click();
  }, []);

  // 백엔드 API 토큰 관리 핸들러
  const handleTokenConfirm = useCallback((token: string) => {
    setBearerToken(token);
    setShowTokenModal(false);
  }, []);

  const handleTokenClear = useCallback(() => {
    clearBearerToken();
    setShowTokenModal(false);
  }, []);

  return (
    <div style={{ width: '100%', height: '100vh', display: 'flex' }}>
      <Sidebar />
      <div style={{ flexGrow: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
        {/* 툴바 버튼 */}
        <div style={toolbarStyle}>
          <button onClick={onExecute} style={executeButtonStyle} disabled={isRunning}>
            {isRunning ? '⏳ 실행 중...' : '▶️ 실행'}
          </button>
          <button onClick={onSave} style={buttonStyle}>저장</button>
          <button onClick={onLoad} style={buttonStyle}>불러오기</button>
          <button onClick={handleEncryptedSave} style={encryptedButtonStyle}>
            🔒 암호화 저장
          </button>
          <button onClick={handleEncryptedLoad} style={encryptedButtonStyle}>
            🔓 암호화 불러오기
          </button>
          <button onClick={onClear} style={buttonStyle}>초기화</button>
          <button
            onClick={() => setShowTokenModal(true)}
            style={hasBearerToken() ? tokenActiveButtonStyle : tokenButtonStyle}
          >
            {hasBearerToken() ? '🔑 설정됨' : '🔑 API 토큰'}
          </button>
        </div>

        <div style={{ flexGrow: 1, position: 'relative', minHeight: 0 }}>
          <ReactFlow
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onConnect={onConnect}
            onDrop={onDrop}
            onDragOver={onDragOver}
            onNodeClick={onNodeClick}
            onPaneClick={onPaneClick}
            nodeTypes={nodeTypes}
            fitView
          >
            <Controls />
            <MiniMap />
            <Background variant={BackgroundVariant.Dots} gap={12} size={1} />
          </ReactFlow>
        </div>
      </div>

      {/* 노드 설정 패널 */}
      {selectedNode && !showChat && (
        <NodeConfigPanel
          selectedNode={{ id: selectedNode.id, ...selectedNode.data }}
          onClose={() => setSelectedNode(null)}
          onSave={handleSaveNodeConfig}
          onOpenChat={
            selectedNode.data.agentType === 'llm'
              ? handleOpenChat
              : undefined
          }
        />
      )}

      {/* LLM 채팅 인터페이스 */}
      {showChat && chatConfig && (
        <ChatInterface
          nodeConfig={chatConfig}
          onClose={() => setShowChat(false)}
        />
      )}

      {/* 실행 결과 패널 */}
      {showResults && (
        <ExecutionPanel
          results={executionResults}
          isRunning={isRunning}
          onClose={() => setShowResults(false)}
        />
      )}

      {/* 암호화 비밀번호 모달 */}
      <PasswordModal
        isOpen={showPasswordModal}
        mode={passwordMode}
        onConfirm={handlePasswordConfirm}
        onCancel={() => setShowPasswordModal(false)}
      />

      {/* 백엔드 API 토큰 설정 모달 */}
      <BackendTokenModal
        isOpen={showTokenModal}
        onConfirm={handleTokenConfirm}
        onCancel={() => setShowTokenModal(false)}
        onClear={handleTokenClear}
        hasExistingToken={hasBearerToken()}
      />
    </div>
  );
}

const toolbarStyle: React.CSSProperties = {
  display: 'flex',
  flexWrap: 'wrap',
  justifyContent: 'flex-end',
  gap: '10px',
  padding: '10px',
  zIndex: 4,
};

const executeButtonStyle: React.CSSProperties = {
  padding: '8px 16px',
  backgroundColor: '#10b981',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

const buttonStyle: React.CSSProperties = {
  padding: '8px 16px',
  backgroundColor: '#1a192b',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
};

const encryptedButtonStyle: React.CSSProperties = {
  padding: '8px 16px',
  backgroundColor: '#6366f1',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

const tokenButtonStyle: React.CSSProperties = {
  padding: '8px 16px',
  backgroundColor: '#f59e0b',
  color: 'white',
  border: 'none',
  borderRadius: '4px',
  cursor: 'pointer',
  fontSize: '14px',
  fontWeight: '600',
};

const tokenActiveButtonStyle: React.CSSProperties = {
  ...tokenButtonStyle,
  backgroundColor: '#059669',
};

export default App;
